package com.kaeonx.moneymanager.activities

import android.content.Intent
import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaeonx.moneymanager.customclasses.MutableLiveData2

private const val TAG = "authVM"

class AuthViewModel : ViewModel() {

    companion object {
        var userId: String? = null
            private set
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
        Log.d(TAG, "userId is $userId")
        it
    }

    init {
        Log.d(TAG, "userId is $userId")
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
//            .setLogo(R.drawable.baseline_account_circle_24) // Set logo drawable
            .build()
    }

    internal fun logout() {
        AuthUI.getInstance()
            .signOut(App.context)
            .addOnCompleteListener {
                refreshAuthMLD()
            }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Storage Related
     * (experiment whether writes are cached locally and persists after app reload!)
     */
    ////////////////////////////////////////////////////////////////////////////////
//    private val context = getApplication<Application>().applicationContext

    private val storage = Firebase.storage
}