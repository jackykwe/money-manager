package com.kaeonx.moneymanager.activities

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

private const val TAG = "authVM"

class AuthViewModel : ViewModel() {

    ////////////////////////////////////////////////////////////////////////////////
    /**
     *  Authentication Related
     */
    ////////////////////////////////////////////////////////////////////////////////

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    init {
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