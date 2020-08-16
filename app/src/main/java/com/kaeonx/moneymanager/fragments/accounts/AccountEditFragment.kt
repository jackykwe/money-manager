package com.kaeonx.moneymanager.fragments.accounts

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.adapters.ColourFamilyPickerArrayAdapter
import com.kaeonx.moneymanager.databinding.FragmentAccountEditBinding

class AccountEditFragment : Fragment() {
    // TODO: NOT OPTIMISED YET FOR SMOOTHNESS - INTRODUCE SOME DELAYS?
    private lateinit var binding: FragmentAccountEditBinding

    private val args: AccountEditFragmentArgs by navArgs()
    private val viewModelFactory by lazy { AccountEditViewModelFactory(args.oldAccount) }
    private val viewModel: AccountEditViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            menu.clear()
            inflateMenu(R.menu.fragment_general_edit_default)
            if (args.deletable) inflateMenu(R.menu.fragment_general_edit_deleteable)

            setOnMenuItemClickListener {
                // Close the keyboard, if it's open
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                when (it.itemId) {
                    R.id.app_bar_delete -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete account \"${viewModel.currentAccount.value!!.name}\"?")
                            .setMessage("The transactions under \"${viewModel.currentAccount.value!!.name}\" will not be deleted.")
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteOldAccount()
                            }
                            .setNegativeButton(R.string.cancel) { _, _ -> }
                            .create()
                            .show()
                        true
                    }
                    R.id.app_bar_save -> {
                        viewModel.saveBTClicked()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }

        binding = FragmentAccountEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // Courtesy of https://stackoverflow.com/a/48185496/7254995
        // Disables typing
        binding.colourFamilySpinner.apply {
            inputType = InputType.TYPE_NULL
            setAdapter(ColourFamilyPickerArrayAdapter(requireContext()))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.focusClearer.setOnFocusChangeListener { _, focused ->
            if (focused) {
                // Close the keyboard, if it's open
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            }
        }

        viewModel.showSnackBarText.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            viewModel.snackBarShown()
        }
        viewModel.navigateUp.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.navigateUpHandled()
                findNavController().navigateUp()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // This callback will only be called when this fragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            isEnabled = true

            // Close the keyboard, if it's open
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            if (viewModel.changesWereMade()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Abandon unsaved changes?")
                    .setPositiveButton(R.string.ok) { _, _ -> findNavController().navigateUp() }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .create()
                    .show()
            } else {
                findNavController().navigateUp()
            }
        }

        // Sets behaviour of UP button to be the same as BACK button (callback in onBackPressedDispatcher)
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }
}