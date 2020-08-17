package com.kaeonx.moneymanager.activities

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler
import java.io.File

private const val TAG = "authVM"

class AuthViewModel : ViewModel() {

    companion object {

        internal var userId: String? = null
            private set

        ////////////////////////////////////////////////////////////////////////////////
        /**
         * Storage Related
         * (experiment whether writes are cached locally and persists after app reload!)
         */
        ////////////////////////////////////////////////////////////////////////////////
//        private val context = getApplication<Application>().applicationContext

        private val storage by lazy {
            Firebase.storage.apply {
                maxUploadRetryTimeMillis = 3000L
            }
        }
        private val storageRef by lazy { storage.reference.child("user_data") }

        private fun generateUserRef(localUserId: String): StorageReference =
            storageRef.child("user_database_$localUserId")

        internal fun uploadJSON(localUserId: String): UploadTask {
            val userRef = generateUserRef(localUserId)
            val dbFile = File(IEFileHandler.buildUserFilePath(localUserId))
            return userRef.putFile(Uri.fromFile(dbFile))
        }

        internal fun deleteJSON(localUserId: String): Task<Void> =
            generateUserRef(localUserId).delete()

        internal fun downloadDatabase(localUserId: String) {
            val userRef = generateUserRef(localUserId)
            val dbFile = File(IEFileHandler.buildUserFilePath(localUserId))

            val uploadTask = userRef.getFile(Uri.fromFile(dbFile))
            uploadTask.addOnFailureListener { exception ->
                Log.d(
                    TAG,
                    "uploadTask: failed, with exception $exception, errorCode ${(exception as StorageException).errorCode}, message ${exception.message}, cause ${exception.cause}, stacktrace ${exception.stackTrace.joinToString(
                        "\n"
                    )}"
                )
                uploadTask.cancel()
            }
            uploadTask.addOnSuccessListener { taskSnapshot ->
                Log.d(
                    TAG,
                    "uploadTask: succeeded with taskSnapshot $taskSnapshot, bytesTransferred is ${taskSnapshot.bytesTransferred}"
                )
            }
            uploadTask.addOnCanceledListener {
                Log.d(TAG, "uploadTask: cancelled")
            }
            uploadTask.addOnPausedListener { taskSnapshot ->
                Log.d(
                    TAG,
                    "uploadTask: paused with taskSnapshot $taskSnapshot, bytesTransferred is ${taskSnapshot.bytesTransferred}"
                )
            }
            uploadTask.addOnProgressListener { taskSnapshot ->
                Log.d(
                    TAG,
                    "uploadTask: progress listener with taskSnapshot $taskSnapshot, bytesTransferred is ${taskSnapshot.bytesTransferred}"
                )
            }
        }


    }

////////////////////////////////////////////////////////////////////////////////
    /**
     *  Authentication Related
     */
////////////////////////////////////////////////////////////////////////////////

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser = MutableLiveData2<FirebaseUser?>(null)
    val currentUser = Transformations.map(_currentUser) {
        userId = _currentUser.value?.uid
        Log.d(TAG, "Transformation: userId is $userId")
        it
    }

    init {
        Log.d(TAG, "Init: userId is $userId")
        refreshAuthMLD()
    }

    internal fun refreshAuthMLD() {
        _currentUser.value = auth.currentUser
    }

    internal fun loginIntent(): Intent {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Create and launch sign-in intent}
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.firebase_auth_light) // Set logo drawable
            .setTheme(R.style.AppTheme)
            .build()
    }

    internal fun logout() {
        AuthUI.getInstance()
            .signOut(App.context)
            .addOnCompleteListener {
                refreshAuthMLD()
            }
    }

}