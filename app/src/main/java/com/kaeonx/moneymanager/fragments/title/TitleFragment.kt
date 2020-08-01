package com.kaeonx.moneymanager.fragments.title

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.FragmentTitleBinding

private const val TAG = "titleFrag"

class TitleFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private var newLogin = false
    private lateinit var binding: FragmentTitleBinding

    private fun displayLogInSnackbar(text: String = "Please login to access the app's functionality.") {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_INDEFINITE)
            .setBehavior(NoSwipeBehaviour())
            .setAction("LOGIN") { startActivityForResult(authViewModel.loginIntent(), 0) }
            .show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null) {
                displayLogInSnackbar()
            } else {
                if (newLogin) {
                    binding.titleIV.setImageResource(R.drawable.firebase_cloud_firestore_dark)
                    Snackbar.make(requireView(), "Hello, ${it.displayName}!", Snackbar.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToLobbyFragment())
                    }, 1200)
                } else {
                    findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToLobbyFragment())
                }
            }
        }
////        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { } // Handle system back press
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                newLogin = true
                authViewModel.refreshAuthMLD()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.e(TAG, "Login error with ${response?.error?.errorCode}")
                displayLogInSnackbar()
            }
        }
    }
}
