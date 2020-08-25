package com.kaeonx.moneymanager.fragments.importexport

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.StreamDownloadTask
import com.google.firebase.storage.UploadTask
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.CloudMetadata
import com.kaeonx.moneymanager.activities.MainActivityViewModel
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.*
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

// TODO SWITCH ALL BARE FUNS/PROPERTIES TO INTERNAL
internal class CloudFragmentViewModel : ViewModel() {

    private var previousNewProgressText: String? = null

    /**
     * Number of yielded values is `intervalCount + 1`.
     * @param intervalCount The number of times this.next() is called
     * (i.e. the number of actual steps you need to perform).
     */
    private fun generatePercentIterator(intervalCount: Int): Iterator<Int> = sequence {
        var iteration = 0f
        while (iteration < intervalCount) {
            yield((iteration++ / intervalCount * 100).toInt())
        }
    }.iterator()

    private fun generateFailureProgressTVText(): String =
        "${previousNewProgressText!!.take(previousNewProgressText!!.length - 1)} failed"

    private fun declareUpdateUIDone() {
        _updateUI.value = Pair("…", 100)
    }

    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    private fun uploadDataInner(
        taskSnapshot: StreamDownloadTask.TaskSnapshot,
        progressIterator: Iterator<Int>
    ) {
        viewModelScope.launch {
            val cloudMetadata = taskSnapshot.stream.use { inputStream ->
                CloudMetadata.fromInputStream(inputStream)
            }
            if (cloudMetadata.lastKnownLoginMillis > Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp) {
                UserPDS.putDSPBoolean("outdated_login", true)
            }

            ensureActive()
            "Uploading…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            if (UserPDS.getDSPBoolean("outdated_login", false)) {
                withContext(Dispatchers.Main) {
                    _doneUI.value = DoneUIData(
                        resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                        progressTVText = generateFailureProgressTVText(),
                        exceptionText = "uploading from outdated login is not allowed"
                    )
                }
                previousNewProgressText = null
            } else {
                uploadTask =
                    MainActivityViewModel.uploadDBToCloud(Firebase.auth.currentUser!!.uid)
                        .addOnSuccessListener { taskSnapshot ->
                            File(
                                MainActivityViewModel.buildUploadableDBFilePath(
                                    Firebase.auth.currentUser!!.uid
                                )
                            ).run { if (exists()) delete() }
                            UserPDS.putDSPLong(
                                "${Firebase.auth.currentUser!!.uid}_last_upload_time",
                                taskSnapshot.metadata!!.updatedTimeMillis
                            )
                            declareUpdateUIDone()
                            _refreshLastUploadedTV.value = true
                            _doneUI.value = DoneUIData(
                                resultIVDrawableId = R.drawable.mdi_cloud_check_green,
                                progressTVText = "Successfully uploaded data",
                                exceptionText = null
                            )
                            previousNewProgressText = null
                        }
                        .addOnFailureListener { exception ->
                            _doneUI.value =
                                when ((exception as StorageException).errorCode) {
                                    StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                                        DoneUIData(
                                            resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                            progressTVText = generateFailureProgressTVText(),
                                            exceptionText = "connection failure\n" +
                                                    "Check your internet connection."
                                        )
                                    }
                                    else -> {
                                        DoneUIData(
                                            resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                            progressTVText = generateFailureProgressTVText(),
                                            exceptionText = "Unknown exception [Inner ${exception.errorCode}]\n" +
                                                    "Please report this bug."
                                        )
                                    }
                                }
                            previousNewProgressText = null
                        }
                        .addOnCanceledListener { Unit }
            }
        }
    }

    internal fun uploadData() {
        _startUI.value = true
        viewModelScope.launch(Dispatchers.Default) {
            val output = JSONObject()
            val repository = UserRepository.getInstance()
            val progressIterator = generatePercentIterator(10)


            // Database version (for future migrations, if needed)
            ensureActive()
            "Exporting DB Version…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            val version = UserDatabase.getInstance().openHelper.readableDatabase.version
            output.put("db", version)


            // Transactions
            ensureActive()
            "Exporting Transactions…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "transactions",
                IETransactionsHandler.listToJsonArray(
                    repository.exportTransactionsSuspend()
                )
            )

            // Budget
            ensureActive()
            "Exporting Budgets…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "budgets",
                IEBudgetsHandler.listToJsonArray(
                    repository.exportBudgetsSuspend()
                )
            )

            // Debts (TODO)
            ensureActive()
            "Exporting Debts…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }

            // Categories
            ensureActive()
            "Exporting Categories…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "categories",
                IECategoriesHandler.listToJsonArray(
                    repository.exportCategoriesSuspend()
                )
            )

            // Accounts
            ensureActive()
            "Exporting Accounts…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "accounts",
                IEAccountsHandler.listToJsonArray(
                    repository.exportAccountsSuspend()
                )
            )

            // Settings
            ensureActive()
            "Exporting Settings…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "settings",
                IEPreferencesHandler.listToJsonArray(
                    repository.exportPreferencesSuspend()
                )
            )

            ensureActive()
            "Writing to JSON…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            IEFileHandler.saveRootToFile(
                MainActivityViewModel.buildUploadableDBFilePath(Firebase.auth.currentUser!!.uid),
                output.toString()
            )

            ensureActive()
            "Uploading…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            // Check that the current login is still the most recent login
            MainActivityViewModel.downloadMetadataFromCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { taskSnapshot ->
                    uploadDataInner(taskSnapshot, progressIterator)
                }
                .addOnFailureListener { exception ->
                    _doneUI.value = when ((exception as StorageException).errorCode) {
                        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                            DoneUIData(
                                resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                progressTVText = generateFailureProgressTVText(),
                                exceptionText = "connection failure\n" +
                                        "Check your internet connection."
                            )
                        }
                        else -> {
                            DoneUIData(
                                resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                progressTVText = generateFailureProgressTVText(),
                                exceptionText = "Unknown error [Outer ${exception.errorCode}]\n" +
                                        "Please report this bug."
                            )
                        }
                    }
                    previousNewProgressText = null

                }
        }
    }

    private fun deleteDataInner(
        taskSnapshot: StreamDownloadTask.TaskSnapshot,
        progressIterator: Iterator<Int>
    ) {
        viewModelScope.launch {
            val cloudMetadata = taskSnapshot.stream.use { inputStream ->
                CloudMetadata.fromInputStream(inputStream)
            }
            if (cloudMetadata.lastKnownLoginMillis > Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp) {
                UserPDS.putDSPBoolean("outdated_login", true)
            }

            "Deleting cloud data…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            if (UserPDS.getDSPBoolean("outdated_login", false)) {
                withContext(Dispatchers.Main) {
                    _doneUI.value = DoneUIData(
                        resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                        progressTVText = generateFailureProgressTVText(),
                        exceptionText = "deleting from outdated login is not allowed"
                    )
                }
                previousNewProgressText = null
            } else {
                MainActivityViewModel.deleteDBFromCloud(Firebase.auth.currentUser!!.uid)
                    .addOnSuccessListener {
                        UserPDS.removeDSPKeyIfExists("${Firebase.auth.currentUser!!.uid}_last_upload_time")
                        declareUpdateUIDone()
                        _refreshLastUploadedTV.value = true
                        _doneUI.value = DoneUIData(
                            resultIVDrawableId = R.drawable.mdi_cloud_off_outline_green,
                            progressTVText = "Successfully deleted data",
                            exceptionText = null
                        )
                        previousNewProgressText = null
                    }
                    .addOnFailureListener { exception ->
                        _doneUI.value = when ((exception as StorageException).errorCode) {
                            StorageException.ERROR_OBJECT_NOT_FOUND -> {
                                DoneUIData(
                                    resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                    progressTVText = generateFailureProgressTVText(),
                                    exceptionText = "nothing to delete"
                                )
                            }
                            StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                                DoneUIData(
                                    resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                    progressTVText = generateFailureProgressTVText(),
                                    exceptionText = "delete failed\n" +
                                            "Check your internet connection."
                                )
                            }
                            else -> {
                                DoneUIData(
                                    resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                    progressTVText = generateFailureProgressTVText(),
                                    exceptionText = "delete failed [${exception.errorCode}]\n" +
                                            "Please report this bug."
                                )
                            }
                        }
                        previousNewProgressText = null
                    }
            }
        }
    }

    internal fun deleteData() {
        _startUI.value = true
        viewModelScope.launch(Dispatchers.Default) {
            val progressIterator = generatePercentIterator(2)

            "Deleting cloud data…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            // Check that the current login is still the most recent login
            MainActivityViewModel.downloadMetadataFromCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { taskSnapshot ->
                    deleteDataInner(taskSnapshot, progressIterator)
                }
                .addOnFailureListener { exception ->
                    _doneUI.value = when ((exception as StorageException).errorCode) {
                        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                            DoneUIData(
                                resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                progressTVText = generateFailureProgressTVText(),
                                exceptionText = "connection failure\n" +
                                        "Check your internet connection."
                            )
                        }
                        else -> {
                            DoneUIData(
                                resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                                progressTVText = generateFailureProgressTVText(),
                                exceptionText = "Unknown error [Outer: ${exception.errorCode}]\n" +
                                        "Please report this bug."
                            )
                        }
                    }
                    previousNewProgressText = null
                }
        }
    }

    private val _startUI = MutableLiveData2(false)
    internal val startUI: LiveData<Boolean>
        get() = _startUI

    internal fun startUIHandled() {
        _startUI.value = false
    }

    private val _updateUI = MutableLiveData2<Pair<String, Int>?>(null)
    internal val updateUI: LiveData<Pair<String, Int>?>
        get() = _updateUI

    internal fun updateUIHandled() {
        _updateUI.value = null
    }

    private val _doneUI = MutableLiveData2<DoneUIData?>(null)
    internal val doneUI: LiveData<DoneUIData?>
        get() = _doneUI

    internal fun doneUIHandled() {
        _doneUI.value = null
    }

    private val _refreshLastUploadedTV = MutableLiveData2(true)
    internal val refreshLastUploadedTV: LiveData<Boolean>
        get() = _refreshLastUploadedTV

    internal fun refreshLastUploadedTVHandled() {
        _refreshLastUploadedTV.value = false
    }

    override fun onCleared() {
        uploadTask?.cancel()
    }
}

internal data class DoneUIData(
    internal val resultIVDrawableId: Int,
    internal val progressTVText: String,
    internal val exceptionText: String?
)