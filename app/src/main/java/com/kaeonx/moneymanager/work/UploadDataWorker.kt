package com.kaeonx.moneymanager.work

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StreamDownloadTask
import com.kaeonx.moneymanager.activities.ActivityViewModel
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.CloudMetadata
import com.kaeonx.moneymanager.activities.Debug
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

private const val TAG = "UploadDataWorker"

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
                Log.d(
                    TAG,
                    "CREATING(REPLACE) WORK with freq ${
                        UserPDS.getString("dap_auto_backup_freq").toLong()
                    } days"
                )
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
            UserPDS.putDSPBoolean("non_guest_outdated_login", true)
        }
        if (UserPDS.getDSPBoolean("non_guest_outdated_login", false)) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_NAME)
            return Result.failure()
        } else {
            var result: Result? = null
            ActivityViewModel.uploadDBJSONToCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { innerTaskSnapshot ->
                    UserPDS.putDSPLong(
                        "${Firebase.auth.currentUser!!.uid}_last_upload_time",
                        innerTaskSnapshot.metadata!!.updatedTimeMillis
                    )
                    Log.d(TAG, "DO WORK RAN SUCCESSFULLY, OH YEAH!?")
                    result = Result.success()
                }
                .addOnFailureListener { exception ->
                    Debug.extendedDebug(TAG, "uploadDataInner", exception)
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
                Result.retry().also {
                    Log.d(
                        TAG,
                        "retrying doWork(): because timeout in Step 2"
                    )
                }
            } else {
                result!!
            }
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
        val currentUserId: String
        try {
            Log.d(TAG, "doWork() called and starting in try block")
            currentUserId = Firebase.auth.currentUser!!.uid.also {
                Log.d(
                    TAG,
                    "doWork(): currentUserId succeeded with currentUser $it"
                )
            }
        } catch (e: NullPointerException) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_NAME)
            return@coroutineScope Result.failure()
        }
        try {
            UserDatabase.getInstance()
            UserDatabase.dropInstance()  // Ensures database is closed properly
            delay(1000L)  // delay just in case the closing takes time

            val file = File(ActivityViewModel.buildUploadableDBFilePath(currentUserId))
            if (!file.exists()) {
                val output = JSONObject()
                val repository = UserRepository.getInstance()

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

                // Debts (TODO)
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
                    ActivityViewModel.buildUploadableDBFilePath(Firebase.auth.currentUser!!.uid),
                    output.toString()
                )
            }

            // Check that the current login is still the most recent login
            // Code from CloudFragmentViewModel
            var mostRecentLoginCheckSuccess: Boolean? = null
            lateinit var usableTaskSnapshot: StreamDownloadTask.TaskSnapshot
            ActivityViewModel.downloadMetadataJSONFromCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { taskSnapshot ->
                    usableTaskSnapshot = taskSnapshot
                    mostRecentLoginCheckSuccess = true
                }
                .addOnFailureListener { exception ->
                    mostRecentLoginCheckSuccess = false
                    Debug.extendedDebug(
                        TAG,
                        "doWork (mostRecentLoginCheck onFailureListener)",
                        exception
                    )
                }
            val timeout = withTimeoutOrNull(10000L) {
                while (mostRecentLoginCheckSuccess == null) {
                    delay(900L)
                }
                true
            }
            return@coroutineScope when {
                timeout == null -> Result.retry().also {
                    Log.d(
                        TAG,
                        "retrying doWork(): because timeout for Step 1"
                    )
                }
                mostRecentLoginCheckSuccess!! -> doWork2(usableTaskSnapshot)
                else -> Result.retry()
            }
        } catch (e: Exception) {
            Debug.extendedDebug(TAG, "doWork", e)
            return@coroutineScope Result.retry()
        }
    }

}