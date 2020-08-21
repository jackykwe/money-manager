package com.kaeonx.moneymanager.fragments.title

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
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.storage.StorageException
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.ActivityViewModel
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.FragmentLobbyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "exitlobf"

class ExitLobbyFragment : Fragment() {

    private lateinit var binding: FragmentLobbyBinding
    private val viewModel: ExitLobbyViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()

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

    private fun handleExceptionAndBacktrack(exception: Exception) {
        when (exception) {
            is FirebaseNetworkException -> {
                Snackbar.make(
                    binding.root,
                    "Unable to logout.\nPlease check your internet connection.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            is StorageException -> {
                when (exception.errorCode) {
                    StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                        Snackbar.make(
                            binding.root,
                            "Unable to logout.\nPlease check your internet connection.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        Snackbar.make(
                            binding.root,
                            "Unable to logout [${exception.errorCode}].\nPlease try again later.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
            else -> {
                Snackbar.make(
                    binding.root,
                    "Unable to logout [Unknown error].\nPlease report this bug.",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setBehavior(NoSwipeBehaviour())
                    .show()
                return
            }
        }

        lifecycleScope.launch {
            delay(1000L)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Snackbar.make(binding.root, "Logging out… Please wait.", Snackbar.LENGTH_INDEFINITE)
            .setBehavior(NoSwipeBehaviour())
            .show()
        viewModel.initialiseExit()

        viewModel.exception.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            viewModel.exceptionHandled()
            handleExceptionAndBacktrack(it)
        }

        viewModel.activityVMDelete.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.activityVMDeleteHandled()
                activityViewModel.delete()  // fails immediately if offline
                    .addOnSuccessListener {
                        // NB: By now, Firebase.auth.currentUser is already null
                        viewModel.deleteEverythingAndExit()
                    }
                    .addOnFailureListener { exception ->
                        handleExceptionAndBacktrack(exception)
                    }
            }
        }

        viewModel.activityVMLogout.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.activityVMLogoutHandled()
                activityViewModel.logout()  // confirm success, even if offline
                viewModel.deleteEverythingAndExit()
            }
        }

        viewModel.recreate.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.recreateHandled()
                lifecycleScope.launch {
                    delay(1000L)
                    requireActivity().recreate()
                }
            }
        }

        viewModel.exit.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.exitHandled()
                lifecycleScope.launch(Dispatchers.Main) {
                    val animDuration =
                        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                    binding.root.animate()
                        .alpha(0f)
                        .setDuration(animDuration)
                        .setListener(null)
                    delay(animDuration)
                    (requireActivity() as MainActivity)
                        .binding
                        .appBarMainInclude
                        .mainActivityToolbar
                        .visibility = View.GONE
                    delay(animDuration)
                    findNavController().run {
                        if (currentDestination?.id == R.id.exitLobbyFragment) {
                            navigate(ExitLobbyFragmentDirections.actionExitLobbyFragmentToTitleFragment())
                        }
                    }
                }
            }
        }
    }
}