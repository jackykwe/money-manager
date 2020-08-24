package com.kaeonx.moneymanager.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.customclasses.MutableLiveData2

class SettingsViewModel : ViewModel() {

    ////////////////////////////////////////////////////////////////////////////////
    /**
     *  Authentication Related
     */
    ////////////////////////////////////////////////////////////////////////////////

    private val _currentUser = MutableLiveData2(Firebase.auth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    private fun refreshAuthMLD() {
        _currentUser.value = Firebase.auth.currentUser
    }

    internal fun updateName(newName: String): Task<Void> {
        val profileUpdates = userProfileChangeRequest { displayName = newName }
        val returnable = Firebase.auth.currentUser!!.updateProfile(profileUpdates)
        returnable.addOnCompleteListener { _ ->
            refreshAuthMLD()
        }
        return returnable
    }

}