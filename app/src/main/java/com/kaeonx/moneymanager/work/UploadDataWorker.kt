package com.kaeonx.moneymanager.work

import android.content.Context
import androidx.work.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StreamDownloadTask
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.CloudMetadata
import com.kaeonx.moneymanager.activities.MainActivityViewModel
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.*
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class UploadDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        internal const val WORK_NAME = "MMUploadDataWorker"

        /**
         * Should be called with GlobalScope
         */
        internal suspend fun cancelWork() =
            WorkManager.getInstance(App.context).cancelUniqueWork(WORK_NAME)
                .await()

        /**
         * Should be called with GlobalScope
         */
        internal suspend fun overwriteWork() {
            if (!UserPDS.getBoolean("dap_auto_backup_enabled") || Firebase.auth.currentUser!!.isAnonymous) {
                cancelWork()
            } else {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresDeviceIdle(false)
                    .build()

                val repeatingRequest =
                    PeriodicWorkRequestBuilder<UploadDataWorker>(
                        UserPDS.getString("dap_auto_backup_freq").toLong(),
                        TimeUnit.DAYS
                    )
                        .setInitialDelay(1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                            BackoffPolicy.EXPONENTIAL,
                            PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                            TimeUnit.MILLISECONDS
                        )
                        .addTag("frequency is ${UserPDS.getString("dap_auto_backup_freq")}")
                        .build()

                WorkManager.getInstance(App.context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    repeatingRequest
                )
            }
        }

    }

    private suspend fun doWork2(outerTaskSnapshot: StreamDownloadTask.TaskSnapshot): Result {
        val cloudMetadata = outerTaskSnapshot.stream.use { inputStream ->
            CloudMetadata.fromInputStream(inputStream)
        }
        if (cloudMetadata.lastKnownLoginMillis > Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp) {
            UserPDS.putDSPBoolean("outdated_login", true)
        }
        if (UserPDS.getDSPBoolean("outdated_login", false)) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_NAME)
            return Result.failure()
        } else {
            var result: Result? = null
            MainActivityViewModel.uploadDBToCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { innerTaskSnapshot ->
                    File(
                        MainActivityViewModel.buildUploadableDBFilePath(
                            Firebase.auth.currentUser!!.uid
                        )
                    ).run { if (exists()) delete() }
                    UserPDS.putDSPLong(
                        "${Firebase.auth.currentUser!!.uid}_last_upload_time",
                        innerTaskSnapshot.metadata!!.updatedTimeMillis
                    )
                    result = Result.success()
                }
                .addOnFailureListener { _ ->
                    result = Result.retry()
                }
                .addOnCanceledListener { Unit }
            val uploadTimeout = withTimeoutOrNull(30000L) {
                while (result == null) {
                    delay(950L)
                }
                true
            }
            return if (uploadTimeout == null) {
                // Timed out
                Result.retry()
            } else {
                result!!
            }
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
        val currentUserId: String
        try {
            currentUserId = Firebase.auth.currentUser!!.uid
        } catch (e: NullPointerException) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_NAME)
            return@coroutineScope Result.failure()
        }
        try {
            UserDatabase.getInstance()
            UserDatabase.dropInstance()  // Ensures database is closed properly
            delay(1000L)  // delay just in case the closing takes time

            val file = File(MainActivityViewModel.buildUploadableDBFilePath(currentUserId))
            if (!file.exists()) {
                val output = JSONObject()
                val repository = UserRepository.getInstance()


                // Database version (for future migrations, if needed)
                val version = UserDatabase.getInstance().openHelper.readableDatabase.version
                UserDatabase.dropInstance()
                output.put("db", version)


                // Transactions
                ensureActive()
                output.put(
                    "transactions",
                    IETransactionsHandler.listToJsonArray(
                        repository.exportTransactionsSuspend()
                    )
                )

                // Budget
                ensureActive()
                output.put(
                    "budgets",
                    IEBudgetsHandler.listToJsonArray(
                        repository.exportBudgetsSuspend()
                    )
                )

                // Debts (TODO Future)
                ensureActive()

                // Categories
                ensureActive()
                output.put(
                    "categories",
                    IECategoriesHandler.listToJsonArray(
                        repository.exportCategoriesSuspend()
                    )
                )

                // Accounts
                ensureActive()
                output.put(
                    "accounts",
                    IEAccountsHandler.listToJsonArray(
                        repository.exportAccountsSuspend()
                    )
                )

                // Settings
                ensureActive()
                output.put(
                    "settings",
                    IEPreferencesHandler.listToJsonArray(
                        repository.exportPreferencesSuspend()
                    )
                )

                ensureActive()
                IEFileHandler.saveRootToFile(
                    MainActivityViewModel.buildUploadableDBFilePath(Firebase.auth.currentUser!!.uid),
                    output.toString()
                )
            }

            // Check that the current login is still the most recent login
            // Code from CloudFragmentViewModel
            var mostRecentLoginCheckSuccess: Boolean? = null
            lateinit var usableTaskSnapshot: StreamDownloadTask.TaskSnapshot
            MainActivityViewModel.downloadMetadataFromCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { taskSnapshot ->
                    usableTaskSnapshot = taskSnapshot
                    mostRecentLoginCheckSuccess = true
                }
                .addOnFailureListener { exception ->
                    mostRecentLoginCheckSuccess = false
                }
            val timeout = withTimeoutOrNull(10000L) {
                while (mostRecentLoginCheckSuccess == null) {
                    delay(900L)
                }
                true
            }
            return@coroutineScope when {
                timeout == null -> Result.retry()
                mostRecentLoginCheckSuccess!! -> doWork2(usableTaskSnapshot)
                else -> Result.retry()
            }
        } catch (e: Exception) {
            return@coroutineScope Result.retry()
        }
    }

}