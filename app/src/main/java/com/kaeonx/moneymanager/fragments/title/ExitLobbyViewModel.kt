package com.kaeonx.moneymanager.fragments.title

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.StreamDownloadTask
import com.google.firebase.storage.UploadTask
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.CloudMetadata
import com.kaeonx.moneymanager.activities.MainActivityViewModel
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.*
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class ExitLobbyViewModel : ViewModel() {

    internal fun initialiseExit() {
        UserRepository.dropInstance()
        UserDatabase.dropInstance()
        XERepository.dropInstance()
        when {
            Firebase.auth.currentUser == null -> {
                // You may get here only if the activity is recreated after logout / delete.
                _exit.value = true
            }
            Firebase.auth.currentUser!!.isAnonymous -> _activityVMDelete.value = true
            else -> uploadData()
        }
    }

    internal fun deleteEverythingAndExit() {
        // Delete database
        val file = App.context.getDatabasePath(
            "user_database_${UserPDS.getDSPString("logged_in_uid", "")}"
        )
        File(file.absolutePath + "-shm").run { if (exists()) delete() }
        File(file.absolutePath + "-wal").run { if (exists()) delete() }
        if (file.exists()) file.delete()

        // Delete JSON files
        val uploadableFile = File(
            MainActivityViewModel.buildUploadableDBFilePath(
                UserPDS.getDSPString("logged_in_uid", "")
            )
        )
        val downloadedFile = File(
            MainActivityViewModel.buildDownloadedDBFilePath(
                UserPDS.getDSPString("logged_in_uid", "")
            )
        )
        if (uploadableFile.exists()) uploadableFile.delete()
        if (downloadedFile.exists()) downloadedFile.delete()

        // Remove Default Shared Preferences
        if (UserPDS.getDSPString("dsp_theme", "light") != "light") {
            // Currently in dark mode
            UserPDS.removeAllDSPKeys()
            _recreate.value = true
        } else {
            // Currently in light mode already
            UserPDS.removeAllDSPKeys()
            _exit.value = true
        }
    }

    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    private fun uploadDataInner(taskSnapshot: StreamDownloadTask.TaskSnapshot) {
        viewModelScope.launch {
            val cloudMetadata = taskSnapshot.stream.use { inputStream ->
                CloudMetadata.fromInputStream(inputStream)
            }
            if (cloudMetadata.lastKnownLoginMillis > Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp) {
                UserPDS.putDSPBoolean("outdated_login", true)
            }

            ensureActive()
            if (UserPDS.getDSPBoolean("outdated_login", false)) {
                _activityVMLogout.value = true
            } else {
                uploadTask =
                    MainActivityViewModel.uploadDBToCloud(Firebase.auth.currentUser!!.uid)
                        .addOnSuccessListener {
                            _activityVMLogout.value = true
                        }
                        .addOnFailureListener { exception ->
                            _exception.value = exception
                        }
                        .addOnCanceledListener { Unit }
            }
        }
    }

    private fun uploadData() {
        viewModelScope.launch {
            val output = JSONObject()
            val repository = UserRepository.getInstance()


            // Database version (for future migrations, if needed)
            // Will be closed later on
            val version = UserDatabase.getInstance().openHelper.readableDatabase.version
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

            UserRepository.dropInstance()
            UserDatabase.dropInstance()  // THIS ONE IS IMPORTANT, IF NOT DB TEMP FILES WON'T BE CLEARED UP AND THERE WILL BE CRASH ON RE-LOGIN
            XERepository.dropInstance()

            ensureActive()
            // Check that the current login is still the most recent login
            MainActivityViewModel.downloadMetadataFromCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { taskSnapshot ->
                    uploadDataInner(taskSnapshot)
                }
                .addOnFailureListener { exception ->
                    _exception.value = exception
                }
        }
    }

    private val _exit = MutableLiveData2(false)
    internal val exit: LiveData<Boolean>
        get() = _exit

    internal fun exitHandled() {
        _exit.value = false
    }

    private val _recreate = MutableLiveData2(false)
    internal val recreate: LiveData<Boolean>
        get() = _recreate

    internal fun recreateHandled() {
        _recreate.value = false
    }

    private val _activityVMDelete = MutableLiveData2(false)
    internal val activityVMDelete: LiveData<Boolean>
        get() = _activityVMDelete

    internal fun activityVMDeleteHandled() {
        _activityVMDelete.value = false
    }

    private val _activityVMLogout = MutableLiveData2(false)
    internal val activityVMLogout: LiveData<Boolean>
        get() = _activityVMLogout

    internal fun activityVMLogoutHandled() {
        _activityVMLogout.value = false
    }

    private val _exception = MutableLiveData2<Exception?>(null)
    internal val exception: LiveData<Exception?>
        get() = _exception

    internal fun exceptionHandled() {
        _exception.value = null
    }

    override fun onCleared() {
        uploadTask?.cancel()
    }
}