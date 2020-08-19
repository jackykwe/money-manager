package com.kaeonx.moneymanager.fragments.title

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.FragmentTitleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "titleFrag"

class TitleFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private var newLogin = false
    private lateinit var binding: FragmentTitleBinding

    private fun displayLogInSnackbar(text: String = "Please login to access the app's functionality.") {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE)
            .setBehavior(NoSwipeBehaviour())
            .setAction("LOGIN") { startActivityForResult(authViewModel.loginIntent(), 0) }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun kickStartUI() {
        if (Firebase.auth.currentUser == null) {
            displayLogInSnackbar()
        } else {
            if (newLogin) {
                binding.titleIV.setImageResource(R.drawable.firebase_cloud_firestore_dark)
                Snackbar.make(
                    binding.root,
                    "Hello, ${Firebase.auth.currentUser!!.displayName}!",
                    Snackbar.LENGTH_SHORT
                ).show()
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(1200L)
                    findNavController().run {
                        if (currentDestination?.id == R.id.titleFragment) {
                            navigate(TitleFragmentDirections.actionTitleFragmentToLobbyFragment())
                        }
                    }
                }
            } else {
                findNavController().run {
                    if (currentDestination?.id == R.id.titleFragment) {
                        navigate(TitleFragmentDirections.actionTitleFragmentToLobbyFragment())
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kickStartUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                newLogin = true
                authViewModel.refreshAuthMLD()
                kickStartUI()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
//                val response = IdpResponse.fromResultIntent(data)
//                Log.e(TAG, "Login error with ${response?.error?.errorCode}")
                displayLogInSnackbar()
            }
        }
    }
}
