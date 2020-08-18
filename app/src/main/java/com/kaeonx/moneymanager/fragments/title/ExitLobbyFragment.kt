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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        super.onResume()
        lifecycleScope.launch(Dispatchers.Main) {
            Snackbar.make(binding.root, "Logging out…", Snackbar.LENGTH_INDEFINITE)
                .setBehavior(NoSwipeBehaviour())
                .show()
            UserRepository.dropInstance()
            UserDatabase.dropInstance()
            XERepository.dropInstance()

            Log.d(TAG, "Bef await")
            Firebase.auth.currentUser!!.delete()
                .addOnSuccessListener {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Log.d(TAG, "Aft await")

                        // By now, Firebase.auth.currentUser is already null, so this is needed
                        val deletingUid = UserPDS.getDSPString("logged_in_uid", "")
                        val file = App.context.getDatabasePath("user_database_${deletingUid}")
                        if (file.exists()) file.delete()

                        authViewModel.logout()
                        while (authViewModel.currentUser.value != null) delay(1L)  // ensure currentUser is null too

                        if (UserPDS.getDSPString("logged_in_uid", "nothing") != "nothing") {
                            UserPDS.removeDSPKey("logged_in_uid")
                        }
                        if (UserPDS.getDSPString("dsp_theme", "nothing") != "nothing") {
                            UserPDS.removeDSPKey("dsp_theme")
                            delay(1000L)
                            requireActivity().recreate()
                        }

                        val animDuration =
                            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
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
                }
                .addOnFailureListener { exception ->
                    Log.d(
                        TAG,
                        "uploadTask: failed, with exception $exception, message ${exception.message}, cause ${exception.cause}, stacktrace ${exception.stackTrace.joinToString(
                            "\n"
                        )}"
                    )
                    lifecycleScope.launch(Dispatchers.Main) {
                        Snackbar.make(
                            binding.root,
                            "Unable to logout.\nPlease check your internet connection.",
                            Snackbar.LENGTH_LONG
                        )
                            .setBehavior(NoSwipeBehaviour())
                            .show()
                        delay(2500L)
                        val animDuration =
                            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
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
    }
}