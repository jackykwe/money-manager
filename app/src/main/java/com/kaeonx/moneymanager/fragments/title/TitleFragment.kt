package com.kaeonx.moneymanager.fragments.title

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.activities.MainActivityViewModel
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.FragmentTitleBinding
import com.kaeonx.moneymanager.userrepository.UserPDS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TitleFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentTitleBinding
    private val viewModel: TitleViewModel by viewModels()

    private fun displayLogInSnackbar() {
        Snackbar.make(
            binding.root,
            "Please login to access the app's functionality.",
            Snackbar.LENGTH_INDEFINITE
        )
            .setBehavior(NoSwipeBehaviour())
            .setAction("LOGIN") { startActivityForResult(mainActivityViewModel.loginIntent(), 0) }
            .show()
    }

    private fun kickStartUIAndNavigateToLobby() {
        if (viewModel.newLogin) {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (Firebase.auth.currentUser?.isAnonymous == false) {
            if (!UserPDS.getDSPBoolean("non_guest_sign_in_complete", false)) {
                viewModel.completeLogin()
            } else {
                kickStartUIAndNavigateToLobby()
            }
        } else if (Firebase.auth.currentUser?.isAnonymous == true) {
            kickStartUIAndNavigateToLobby()
        } else {
            displayLogInSnackbar()
        }

        viewModel.completeLoginStart.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.completeLoginStartHandled()
                Snackbar.make(
                    binding.root,
                    "Getting things ready…",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setBehavior(NoSwipeBehaviour())
                    .show()
                binding.titleTV.animate()
                    .alpha(1f)
                    .setDuration(
                        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                    )
                    .setListener(null)
            }
        }

        viewModel.titleTVText.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            viewModel.titleTVTextHandled()
            binding.titleTV.text = it
        }

        viewModel.titleTVExit.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.titleTVExitHandled()
                binding.titleTV.apply {
                    animate()
                        .alpha(0f)
                        .setDuration(
                            resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                        )
                        .setListener(null)
                }
            }
        }

        viewModel.kickStartUIAndNavigateToLobby.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.kickStartUIAndNavigateToLobbyHandled()
                kickStartUIAndNavigateToLobby()
            }
        }

        viewModel.showRetrySnackbar.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            viewModel.showRetrySnackbarHandled()
            Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                .setBehavior(NoSwipeBehaviour())
                .setAction("RETRY") { viewModel.completeLogin() }
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                mainActivityViewModel.refreshAuthMLD()
                if (Firebase.auth.currentUser!!.isAnonymous) {
                    kickStartUIAndNavigateToLobby()
                } else {
                    viewModel.completeLogin()
                }
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
