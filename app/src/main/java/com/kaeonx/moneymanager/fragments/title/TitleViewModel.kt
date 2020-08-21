package com.kaeonx.moneymanager.fragments.title

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.activities.CloudMetadata
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.userrepository.UserPDS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TitleViewModel : ViewModel() {

    internal var newLogin = false
        private set

    private enum class LoginProgress {
        STARTED,
        SUCCESS,
        ERROR_NO_INTERNET,
        ERROR_UNKNOWN
    }

    internal fun completeLogin() {
        _completeLoginStart.value = true

        // 1. [Title] Upload lastKnownLoginMillis to cloud l(using InputStream)
        var writeLastKnownLoginMillisDone = LoginProgress.STARTED
        AuthViewModel.uploadMetadataJSONToCloud(
            Firebase.auth.currentUser!!.uid,
            CloudMetadata.Builder(
                lastKnownLoginMillis = Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp
            )
        )
            .addOnSuccessListener {
                writeLastKnownLoginMillisDone = LoginProgress.SUCCESS
            }
            .addOnFailureListener { exception ->
                writeLastKnownLoginMillisDone =
                    when ((exception as StorageException).errorCode) {
                        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> LoginProgress.ERROR_NO_INTERNET
                        else -> LoginProgress.ERROR_UNKNOWN
                    }
            }

        // 2. [Title] Download JSON Metadata from cloud if exists
        var downloadDBJSONMetadataFromCloudDone = LoginProgress.STARTED
        AuthViewModel.getDBJSONMetadataFromCloud(Firebase.auth.currentUser!!.uid)
            .addOnSuccessListener { metadata ->
                UserPDS.putDSPLong(
                    "${Firebase.auth.currentUser!!.uid}_last_upload_time",
                    metadata.updatedTimeMillis
                )
                downloadDBJSONMetadataFromCloudDone = LoginProgress.SUCCESS
            }
            .addOnFailureListener { exception ->
                downloadDBJSONMetadataFromCloudDone =
                    when ((exception as StorageException).errorCode) {
                        StorageException.ERROR_OBJECT_NOT_FOUND -> LoginProgress.SUCCESS  // User did not upload any cloud data. It's okay.
                        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> LoginProgress.ERROR_NO_INTERNET
                        else -> LoginProgress.ERROR_UNKNOWN
                    }
            }

        // 3. [Title] Download JSON from cloud if exists (creates downloaded_database_<uid>.json)
        // Processing of CloudDB done in LobbyFragment
        var downloadDBJSONFromCloudDone = LoginProgress.STARTED
        AuthViewModel.downloadDBJSONFromCloud(Firebase.auth.currentUser!!.uid)
            .addOnSuccessListener {
                downloadDBJSONFromCloudDone = LoginProgress.SUCCESS
            }
            .addOnFailureListener { exception ->
                downloadDBJSONFromCloudDone = when ((exception as StorageException).errorCode) {
                    StorageException.ERROR_OBJECT_NOT_FOUND -> LoginProgress.SUCCESS  // User did not upload any cloud data. It's okay.
                    StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> LoginProgress.ERROR_NO_INTERNET
                    else -> LoginProgress.ERROR_UNKNOWN
                }
            }

        // Only if 1. and 2. and 3. are done, sets "non_guest_sign_in_complete" flag to true.
        viewModelScope.launch {
            while (downloadDBJSONMetadataFromCloudDone == LoginProgress.STARTED
                || downloadDBJSONFromCloudDone == LoginProgress.STARTED
                || writeLastKnownLoginMillisDone == LoginProgress.STARTED
            ) {
                val string1 = if (writeLastKnownLoginMillisDone == LoginProgress.STARTED)
                    "…" else "OK"
                val string2 = if (downloadDBJSONMetadataFromCloudDone == LoginProgress.STARTED)
                    "…" else "OK"
                val string3 = if (downloadDBJSONFromCloudDone == LoginProgress.STARTED)
                    "…" else "OK"
                _titleTVText.value = App.context.resources.getString(
                    R.string.title_tv_progress,
                    string1,
                    string2,
                    string3
                )
                delay(500L)
            }

            if (downloadDBJSONMetadataFromCloudDone == LoginProgress.SUCCESS
                && downloadDBJSONFromCloudDone == LoginProgress.SUCCESS
                && writeLastKnownLoginMillisDone == LoginProgress.SUCCESS
            ) {
                _titleTVText.value = App.context.resources.getString(
                    R.string.title_tv_final_progress
                )
                UserPDS.putDSPBoolean("non_guest_sign_in_complete", true)
                newLogin = true
                _kickStartUIAndNavigateToLobby.value = true
            } else if (downloadDBJSONMetadataFromCloudDone == LoginProgress.ERROR_NO_INTERNET
                || downloadDBJSONFromCloudDone == LoginProgress.ERROR_NO_INTERNET
                || writeLastKnownLoginMillisDone == LoginProgress.ERROR_NO_INTERNET
            ) {
                _showRetrySnackbar.value =
                    "Unable to login.\nPlease check your internet connection."
            } else {
                _showRetrySnackbar.value =
                    "Error during login.\nPlease try again or report this bug."
            }
            _titleTVExit.value = true
        }
    }

    private val _completeLoginStart = MutableLiveData2(false)
    internal val completeLoginStart: LiveData<Boolean>
        get() = _completeLoginStart

    internal fun completeLoginStartHandled() {
        _completeLoginStart.value = false
    }

    private val _titleTVText = MutableLiveData2<String?>(null)
    internal val titleTVText: LiveData<String?>
        get() = _titleTVText

    internal fun titleTVTextHandled() {
        _titleTVText.value = null
    }

    private val _titleTVExit = MutableLiveData2(false)
    internal val titleTVExit: LiveData<Boolean>
        get() = _titleTVExit

    internal fun titleTVExitHandled() {
        _titleTVExit.value = false
    }

    private val _kickStartUIAndNavigateToLobby = MutableLiveData2(false)
    internal val kickStartUIAndNavigateToLobby: LiveData<Boolean>
        get() = _kickStartUIAndNavigateToLobby

    internal fun kickStartUIAndNavigateToLobbyHandled() {
        _kickStartUIAndNavigateToLobby.value = false
    }

    private val _showRetrySnackbar = MutableLiveData2<String?>(null)
    internal val showRetrySnackbar: LiveData<String?>
        get() = _showRetrySnackbar

    internal fun showRetrySnackbarHandled() {
        _showRetrySnackbar.value = null
    }

}