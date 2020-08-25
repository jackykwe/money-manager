package com.kaeonx.moneymanager.activities

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.*
import com.google.firebase.storage.ktx.storage
import com.kaeonx.moneymanager.BuildConfig
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.work.UploadDataWorker
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    companion object {

        ////////////////////////////////////////////////////////////////////////////////
        /**
         * Storage Related
         * (experiment whether writes are cached locally and persists after app reload!)
         */
        ////////////////////////////////////////////////////////////////////////////////
//        private val context = getApplication<Application>().applicationContext

        private val storage by lazy {
            Firebase.storage.apply {
                // To force errors to propagate immediately
                maxUploadRetryTimeMillis = 0L
                maxDownloadRetryTimeMillis = 0L
                maxOperationRetryTimeMillis = 0L
            }
        }

        private fun userRoot(userId: String): StorageReference =
            storage.reference.child(userId)

        ////////////////////////////////////////////////////////////////////////////////
        // User Database JSON
        private fun userDBRef(userId: String): StorageReference =
            userRoot(userId).child("database_$userId.json")

        internal fun buildUploadableDBFilePath(uid: String): String {
            return App.context.filesDir.path + "/uploadable_database_$uid.json"
        }

        internal fun buildDownloadedDBFilePath(uid: String): String {
            return App.context.filesDir.path + "/downloaded_database_$uid.json"
        }

        internal fun uploadDBToCloud(userId: String): UploadTask {
            val userRef = userDBRef(userId)
            val dbFile = File(buildUploadableDBFilePath(userId))
            return userRef.putFile(Uri.fromFile(dbFile))
        }

        internal fun getDBMetadataFromCloud(userId: String): Task<StorageMetadata> =
            userDBRef(userId).metadata

        internal fun downloadDBFromCloud(userId: String): FileDownloadTask {
            val userRef = userDBRef(userId)
            val dbFile = File(buildDownloadedDBFilePath(userId))
            return userRef.getFile(Uri.fromFile(dbFile))
        }

        internal fun deleteDBFromCloud(userId: String): Task<Void> =
            userDBRef(userId).delete()

        ////////////////////////////////////////////////////////////////////////////////
        // User Login JSON
        private fun userMetadataRef(userId: String): StorageReference =
            userRoot(userId).child("metadata_$userId.json")

        /**
         * Convenience function
         */
        internal fun uploadNewMetadataToCloud(): UploadTask {
            return uploadMetadataToCloud(
                Firebase.auth.currentUser!!.uid,
                CloudMetadata(
                    lastKnownLoginMillis = Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp,
                    lastKnownOnline = System.currentTimeMillis(),
                    lastKnownOnlineVersion = BuildConfig.VERSION_NAME
                )
            )
        }

        internal fun uploadMetadataToCloud(
            userId: String,
            cloudMetadata: CloudMetadata
        ): UploadTask {
            val userRef = userMetadataRef(userId)
            val stream = cloudMetadata.toByteInputStream()
            return userRef.putStream(stream)
        }

        internal fun downloadMetadataFromCloud(userId: String): StreamDownloadTask {
            val userRef = userMetadataRef(userId)
            return userRef.stream
        }

//        internal fun deleteMetadataFromCloud(userId: String): Task<Void> =
//            userMetadataRef(userId).delete()

        ////////////////////////////////////////////////////////////////////////////////
        // Superuser JSON
        private fun downloadSuperuserMetadataFromCloud(): StreamDownloadTask {
            val superuserRef = userRoot("SUPERUSER").child("metadata.json")
            return superuserRef.stream
        }

    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     *  Authentication Related
     */
    ////////////////////////////////////////////////////////////////////////////////

    private val _currentUser = MutableLiveData2(Firebase.auth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    internal fun refreshAuthMLD() {
        _currentUser.value = Firebase.auth.currentUser
    }

    internal fun loginIntent(): Intent {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )
        // Create and launch sign-in intent}
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .enableAnonymousUsersAutoUpgrade()
            .setLogo(R.drawable.firebase_auth_light) // Set logo drawable
            .setTheme(R.style.AppTheme)
            .build()
    }

    internal fun loginIntentNoAnonymous(): Intent {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        // Create and launch sign-in intent}
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .enableAnonymousUsersAutoUpgrade()
            .setLogo(R.drawable.firebase_auth_light) // Set logo drawable
            .setTheme(R.style.AppTheme)
            .build()
    }

    internal fun logout() {
        AuthUI.getInstance()
            .signOut(App.context)
    }

    internal fun delete(): Task<Void> = AuthUI.getInstance().delete(App.context)

    ////////////////////////////////////////////////////////////////////////////////
    /**
     *  Initiated from LobbyFragment
     */
    ////////////////////////////////////////////////////////////////////////////////
    internal fun attemptToRefreshRatesTable() {
        viewModelScope.launch { XERepository.getInstance().checkAndUpdateIfNecessary() }
    }

    internal fun attemptFetchAndUpdate() {
        // In sequence:
        // 1. [Lobby] Download lastKnownLoginMillis from cloud (using InputStream)
        // 2. [Lobby] If downloaded lastKnownLoginMillis is greater than Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp
        //            then set "outdated_login" flag to true
        // 3. [Lobby] Upload lastKnownOnlineMillis and LastKnownOnlineVersion to cloud (using InputStream)
        // Operations 1., 2. and 3. will be cancelled if no internet connection is available.
        viewModelScope.launch {
            downloadMetadataFromCloud(Firebase.auth.currentUser!!.uid)
                .addOnSuccessListener { taskSnapshot ->
                    viewModelScope.launch {
                        val cloudMetadata = taskSnapshot.stream.use { inputStream ->
                            CloudMetadata.fromInputStream(inputStream)
                        }
                        uploadMetadataToCloud(
                            Firebase.auth.currentUser!!.uid,
                            cloudMetadata.copy(
                                lastKnownOnline = System.currentTimeMillis(),
                                lastKnownOnlineVersion = BuildConfig.VERSION_NAME
                            )
                        )
                            .addOnSuccessListener { Unit }
                            .addOnFailureListener { Unit }
                        ensureActive()
                        if (cloudMetadata.lastKnownLoginMillis > Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp) {
                            UserPDS.putDSPBoolean("outdated_login", true)
                            withContext(Dispatchers.Main) {
                                _showOutdatedLoginSnackbar.value = true
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    when ((exception as StorageException).errorCode) {
                        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> Unit
                        StorageException.ERROR_OBJECT_NOT_FOUND -> {
                            uploadNewMetadataToCloud()
                                .addOnSuccessListener { Unit }
                                .addOnFailureListener { Unit }
                        }
                        else -> Unit
                    }
                    if (UserPDS.getDSPBoolean("outdated_login", false))
                        _showOutdatedLoginSnackbar.value = true
                }
        }
    }

    internal fun attemptToCheckVersionUpdates() {
        viewModelScope.launch {
            downloadSuperuserMetadataFromCloud()
                .addOnSuccessListener { taskSnapshot ->
                    viewModelScope.launch {
                        val jsonObject = taskSnapshot.stream.use { inputStream ->
                            JSONObject(inputStream.bufferedReader().use { it.readText() })
                        }
                        if (jsonObject.optInt("l", -1) > BuildConfig.VERSION_CODE) {
                            _showOutdatedAppSnackbar.value = true
                        }
                    }
                }
                .addOnFailureListener { Unit }
        }
    }

    private val _showOutdatedAppSnackbar = MutableLiveData2(false)
    internal val showOutdatedAppSnackbar: LiveData<Boolean>
        get() = _showOutdatedAppSnackbar

    internal fun showOutdatedAppSnackbarHandled() {
        _showOutdatedAppSnackbar.value = false
    }

    private val _showOutdatedLoginSnackbar = MutableLiveData2(false)
    internal val showOutdatedLoginSnackbar: LiveData<Boolean>
        get() = _showOutdatedLoginSnackbar

    internal fun showOutdatedLoginSnackbarHandled() {
        _showOutdatedLoginSnackbar.value = false
    }

    internal fun updateWorkStatus() {
        viewModelScope.launch {
            if (!UserPDS.getBoolean("dap_auto_backup_enabled") || Firebase.auth.currentUser!!.isAnonymous) {
                GlobalScope.launch {
                    UploadDataWorker.cancelWork()
                }
            } else {
                if (WorkManager.getInstance(App.context)
                        .getWorkInfosForUniqueWork(UploadDataWorker.WORK_NAME)
                        .await()
                        .run {
                            isEmpty() || this[0].state == WorkInfo.State.CANCELLED
                        }
                ) {
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
                        UploadDataWorker.WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        repeatingRequest
                    )
                }
            }
        }
    }

}