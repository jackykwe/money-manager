package com.kaeonx.moneymanager.fragments.title

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.kaeonx.moneymanager.databinding.FragmentLobbyBinding
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "lobbyfrag"

class LobbyFragment : Fragment() {

    private lateinit var binding: FragmentLobbyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 1. [Lobby] Download lastKnownLoginMillis from cloud (using InputStream)
        // 2. [Lobby] If downloaded lastKnownLoginMillis is greater than Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp
        //            then set "non_guest_outdated_login" flag to true
        // Operations 1. and 2. will be cancelled if no internet connection is available.
        requireActivity().let {
            fun showOutdatedLoginSnackbar() {
                Snackbar.make(
                    (it as MainActivity).binding.root,
                    "Cloud Backup is disabled as you've logged in on another device.",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setBehavior(NoSwipeBehaviour())
                    .setAction("OK") { Unit }
                    .show()
            }

            it.lifecycleScope.launch {
                if (!UserPDS.getDSPBoolean("non_guest_outdated_login", false)) {
                    AuthViewModel.downloadMetadataJSONFromCloud(Firebase.auth.currentUser!!.uid)
                        .addOnSuccessListener { taskSnapshot ->
                            it.lifecycleScope.launch {
                                val cloudMetadata = taskSnapshot.stream.use { inputStream ->
                                    CloudMetadata.fromInputStream(inputStream)
                                }
                                if (cloudMetadata.lastKnownLoginMillis > Firebase.auth.currentUser!!.metadata!!.lastSignInTimestamp) {
                                    Log.d(TAG, "OUTDATED!!")
                                    UserPDS.putDSPBoolean("non_guest_outdated_login", true)
                                    showOutdatedLoginSnackbar()
                                } else {
                                    Log.d(TAG, "Not outdated !!")
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            when ((exception as StorageException).errorCode) {
                                StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                                    Log.e(TAG, "limit exceeded here")
                                }
                                else -> {
                                    Log.e(
                                        TAG,
                                        "downloadMetadataJSONFromCloud: failed, with exception $exception, message ${exception.message}, cause ${exception.cause}, stacktrace ${
                                            exception.stackTrace.joinToString(
                                                "\n"
                                            )
                                        }"
                                    )
                                }
                            }
                        }
                } else {
                    Log.d(TAG, "already outdated")
                    showOutdatedLoginSnackbar()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val xeRepository = XERepository.getInstance()
            val userRepository = UserRepository.getInstance()
            while (
                userRepository.accounts.value == null ||
                userRepository.categories.value == null ||
                userRepository.preferences.value == null ||
                xeRepository.xeRows.value == null
            ) {
                ensureActive()
                delay(1L)
            }

            UserPDS.putDSPString("logged_in_uid", Firebase.auth.currentUser!!.uid)

            val downloadedFile = File(
                AuthViewModel.buildDownloadedDBFilePath(
                    UserPDS.getDSPString("logged_in_uid", "")
                )
            )
            if (downloadedFile.exists()) {
                // IMPORT


//                downloadedFile.delete()
            }

            // Ensure theme is correct
            val userTheme = UserPDS.getString("dsp_theme")
            val sharedPrefTheme = UserPDS.getDSPString("dsp_theme", "light")
            if (sharedPrefTheme != userTheme) {
                UserPDS.putDSPString("dsp_theme", UserPDS.getString("dsp_theme"))
                Snackbar.make(requireView(), "Applying theme…", Snackbar.LENGTH_SHORT).show()
                delay(1000L)
                requireActivity().recreate()
            }

            val animDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            binding.root.animate()
                .alpha(0f)
                .setDuration(animDuration)
                .setListener(null)
            delay(animDuration + 50)
            findNavController().run {
                if (currentDestination?.id == R.id.lobbyFragment) {
                    navigate(LobbyFragmentDirections.actionLobbyFragmentToTransactionsFragment())
                }
            }
        }
    }
}