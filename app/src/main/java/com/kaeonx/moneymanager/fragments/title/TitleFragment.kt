package com.kaeonx.moneymanager.fragments.title

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.storage.StorageException
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.activities.CloudMetadata
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.FragmentTitleBinding
import com.kaeonx.moneymanager.userrepository.UserPDS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "titleFrag"

class TitleFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private var newLogin = false
    private lateinit var binding: FragmentTitleBinding

    private fun displayLogInSnackbar() {
        Snackbar.make(
            binding.root,
            "Please login to access the app's functionality.",
            Snackbar.LENGTH_INDEFINITE
        )
            .setBehavior(NoSwipeBehaviour())
            .setAction("LOGIN") { startActivityForResult(authViewModel.loginIntent(), 0) }
            .show()
    }

    private fun kickStartUIAndNavigateToLobby() {
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

    private enum class LoginProgress {
        STARTED,
        SUCCESS,
        ERROR_NO_INTERNET,
        ERROR_UNKNOWN
    }

    private fun completeLogin() {
        if (Firebase.auth.currentUser!!.isAnonymous) {
            kickStartUIAndNavigateToLobby()
        } else {
            Snackbar.make(
                binding.root,
                "Getting things ready…",
                Snackbar.LENGTH_INDEFINITE
            )
                .setBehavior(NoSwipeBehaviour())
                .show()

            // 1. [Title] Upload lastKnownLoginMillis to cloud l(using InputStream)
            var writeLastKnownLoginMillisDone = LoginProgress.STARTED
            AuthViewModel.uploadMetadataJSONToCloud(
                Firebase.auth.currentUser!!.uid,
                CloudMetadata.Builder(
                    lastKnownLoginMillis = Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp.also {
                        Log.d(
                            TAG,
                            "uploading last login millis: $it"
                        )
                    }
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
            lifecycleScope.launch {
                while (downloadDBJSONMetadataFromCloudDone == LoginProgress.STARTED
                    || downloadDBJSONFromCloudDone == LoginProgress.STARTED
                    || writeLastKnownLoginMillisDone == LoginProgress.STARTED
                ) {
                    delay(1000L)
                }

                if (downloadDBJSONMetadataFromCloudDone == LoginProgress.SUCCESS
                    && downloadDBJSONFromCloudDone == LoginProgress.SUCCESS
                    && writeLastKnownLoginMillisDone == LoginProgress.SUCCESS
                ) {
                    UserPDS.putDSPBoolean("non_guest_sign_in_complete", true)
                    newLogin = true
                    kickStartUIAndNavigateToLobby()
                } else if (downloadDBJSONMetadataFromCloudDone == LoginProgress.ERROR_NO_INTERNET
                    || downloadDBJSONFromCloudDone == LoginProgress.ERROR_NO_INTERNET
                    || writeLastKnownLoginMillisDone == LoginProgress.ERROR_NO_INTERNET
                ) {
                    Snackbar.make(
                        binding.root,
                        "Unable to login.\nPlease check your internet connection.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setBehavior(NoSwipeBehaviour())
                        .setAction("RETRY") { completeLogin() }
                        .show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Error during login.\nPlease try again or report this bug.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setBehavior(NoSwipeBehaviour())
                        .setAction("RETRY") { completeLogin() }
                        .show()
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
                completeLogin()
            } else {
                kickStartUIAndNavigateToLobby()
            }
        } else if (Firebase.auth.currentUser?.isAnonymous == true) {
            kickStartUIAndNavigateToLobby()
        } else {
            displayLogInSnackbar()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                authViewModel.refreshAuthMLD()
                completeLogin()
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
