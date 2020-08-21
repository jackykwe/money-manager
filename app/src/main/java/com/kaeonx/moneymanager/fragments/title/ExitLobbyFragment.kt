package com.kaeonx.moneymanager.fragments.title

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
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.FragmentLobbyBinding
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "exitlobf"

class ExitLobbyFragment : Fragment() {

    private lateinit var binding: FragmentLobbyBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            setNavigationOnClickListener(null)
            navigationIcon = null
            menu.clear()
        }

        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {

        suspend fun exit() = coroutineScope {
            val animDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            binding.root.animate()
                .alpha(0f)
                .setDuration(animDuration)
                .setListener(null)
            delay(animDuration)
            (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.visibility =
                View.GONE
            delay(animDuration)
            findNavController().run {
                if (currentDestination?.id == R.id.exitLobbyFragment) {
                    navigate(ExitLobbyFragmentDirections.actionExitLobbyFragmentToTitleFragment())
                }
            }
        }

        fun deleteLocalDatabase() {
            val file = App.context.getDatabasePath(
                "user_database_${UserPDS.getDSPString("logged_in_uid", "")}"
            )
            if (file.exists()) file.delete()
        }

        fun deleteJSONFiles() {
            val uploadableFile = File(
                AuthViewModel.buildUploadableDBFilePath(
                    UserPDS.getDSPString("logged_in_uid", "")
                )
            )
            if (uploadableFile.exists()) uploadableFile.delete()

            val downloadedFile = File(
                AuthViewModel.buildDownloadedDBFilePath(
                    UserPDS.getDSPString("logged_in_uid", "")
                )
            )
            if (downloadedFile.exists()) downloadedFile.delete()
        }

        fun deleteDSPDspThemeIfExistsAndExit() = lifecycleScope.launch {
            if (UserPDS.getDSPString("dsp_theme", "light") != "light") {
                // Currently in dark mode
                UserPDS.removeDSPKeyIfExists("dsp_theme")
                delay(1000L)
                requireActivity().recreate()
            } else {
                // Currently in light mode already
                UserPDS.removeDSPKeyIfExists("dsp_theme")
                exit()
            }
        }

        super.onResume()
//        lifecycleScope.launch {
//            authViewModel.delete()
//            delay(5000L)
//            authViewModel.delete()
//        }

        Snackbar.make(binding.root, "Logging out…", Snackbar.LENGTH_INDEFINITE)
            .setBehavior(NoSwipeBehaviour())
            .show()
        UserRepository.dropInstance()
        UserDatabase.dropInstance()
        XERepository.dropInstance()

        when {
            Firebase.auth.currentUser == null -> {
                // You may get here only if the activity is recreated after logout / delete.
                Log.d(TAG, "WARN: YOU'RE IN SPECIAL BRANCH")
                lifecycleScope.launch { exit() }
            }
            Firebase.auth.currentUser!!.isAnonymous -> {
                authViewModel.delete()  // fails immediately if offline
                    .addOnSuccessListener {
                        // NB: By now, Firebase.auth.currentUser is already null
                        deleteLocalDatabase()
                        UserPDS.removeDSPKeyIfExists("logged_in_uid")
                        deleteDSPDspThemeIfExistsAndExit()
                    }
                    .addOnFailureListener { exception ->
                        when (exception) {
                            is FirebaseNetworkException -> {
                                Snackbar.make(
                                    binding.root,
                                    "Unable to logout.\nPlease check your internet connection.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            else -> {
                                Snackbar.make(
                                    binding.root,
                                    "Unable to logout.\nPlease try again later.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                        lifecycleScope.launch {
                            delay(2000L)
                            val animDuration = resources
                                .getInteger(android.R.integer.config_shortAnimTime)
                                .toLong()
                            binding.root.animate()
                                .alpha(0f)
                                .setDuration(animDuration)
                                .setListener(null)
                            delay(animDuration + 50)
                            findNavController().run {
                                if (currentDestination?.id == R.id.exitLobbyFragment) {
                                    navigate(ExitLobbyFragmentDirections.actionExitLobbyFragmentToTransactionsFragment())
                                }
                            }
                        }
                    }
            }
            else -> {
                authViewModel.logout()  // confirm success, even if offline
                deleteLocalDatabase()
                deleteJSONFiles()
                UserPDS.removeDSPKeyIfExists(
                    "${UserPDS.getDSPString("logged_in_uid", "")}_last_upload_time"
                )
                UserPDS.removeDSPKeyIfExists("non_guest_sign_in_complete")
                UserPDS.removeDSPKeyIfExists("non_guest_outdated_login")
                UserPDS.removeDSPKeyIfExists("logged_in_uid")
                deleteDSPDspThemeIfExistsAndExit()
            }
        }
    }
}