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
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.activities.MainActivityViewModel
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.FragmentLobbyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LobbyFragment : Fragment() {

    private lateinit var binding: FragmentLobbyBinding
    private val viewModel: LobbyViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivityViewModel.attemptToFetchLastKnownLoginMillis()
        viewModel.initialise()

        viewModel.showErrorSnackbar.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            viewModel.showErrorSnackbarHandled()
            Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                .setBehavior(NoSwipeBehaviour())
                .setAction("OK") { Unit }
                .show()
        }

        viewModel.showApplyingThemeSnackbar.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.showApplyingThemeSnackbarHandled()
                lifecycleScope.launch(Dispatchers.Main) {
                    Snackbar.make(requireView(), "Applying theme…", Snackbar.LENGTH_SHORT).show()
                    delay(1000L)
                    requireActivity().recreate()
                }
            }
        }

        viewModel.initDone.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.initDoneHandled()
                mainActivityViewModel.updateWorkStatus()
                lifecycleScope.launch(Dispatchers.Main) {
                    val animDuration =
                        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
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
    }

}