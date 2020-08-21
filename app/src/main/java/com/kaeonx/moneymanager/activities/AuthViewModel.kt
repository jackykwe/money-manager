package com.kaeonx.moneymanager.activities

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.*
import com.google.firebase.storage.ktx.storage
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import java.io.File

private const val TAG = "authVM"

class AuthViewModel : ViewModel() {

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
        private val storageRef by lazy { storage.reference.child("user_data") }

        ////////////////////////////////////////////////////////////////////////////////

        private fun generateCloudDBStorageRef(userId: String): StorageReference =
            storageRef.child("database_$userId.json")

        internal fun buildUploadableDBFilePath(uid: String): String {
            return App.context.filesDir.path + "/uploadable_database_$uid.json"
        }

        internal fun buildDownloadedDBFilePath(uid: String): String {
            return App.context.filesDir.path + "/downloaded_database_$uid.json"
        }

        internal fun uploadDBJSONToCloud(userId: String): UploadTask {
            val userRef = generateCloudDBStorageRef(userId)
            val dbFile = File(buildUploadableDBFilePath(userId))
            return userRef.putFile(Uri.fromFile(dbFile))
        }

        internal fun deleteDBJSONFromCloud(userId: String): Task<Void> =
            generateCloudDBStorageRef(userId).delete()

        internal fun downloadDBJSONFromCloud(userId: String): FileDownloadTask {
            val userRef = generateCloudDBStorageRef(userId)
            val dbFile = File(buildDownloadedDBFilePath(userId))
            return userRef.getFile(Uri.fromFile(dbFile))
        }

        internal fun getDBJSONMetadataFromCloud(userId: String): Task<StorageMetadata> =
            generateCloudDBStorageRef(userId).metadata

        ////////////////////////////////////////////////////////////////////////////////

        private fun generateCloudMetadataStorageRef(userId: String): StorageReference =
            storageRef.child("metadata_$userId.json")

        internal fun uploadMetadataJSONToCloud(
            userId: String,
            metadataBuilder: CloudMetadata.Builder
        ): UploadTask {
            val userRef = generateCloudMetadataStorageRef(userId)
            val stream = metadataBuilder.toByteInputStream()
            return userRef.putStream(stream)
        }

        internal fun deleteMetadataJSONFromCloud(userId: String): Task<Void> =
            generateCloudMetadataStorageRef(userId).delete()

        internal fun downloadMetadataJSONFromCloud(userId: String): StreamDownloadTask {
            val userRef = generateCloudMetadataStorageRef(userId)
            return userRef.stream
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
            AuthUI.IdpConfig.GoogleBuilder().build(),
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
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
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

    internal fun delete(): Task<Void> =
        AuthUI.getInstance()
            .delete(App.context)
}