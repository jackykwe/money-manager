package com.kaeonx.moneymanager.fragments.budget

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentBudgetEditBinding
import kotlinx.coroutines.launch

class BudgetEditFragment : Fragment() {

    private lateinit var binding: FragmentBudgetEditBinding

    private val args: BudgetEditFragmentArgs by navArgs()
    private val viewModelFactory by lazy { BudgetEditViewModelFactory(args.oldBudget) }
    private val viewModel: BudgetEditViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            menu.clear()
            inflateMenu(R.menu.fragment_general_edit_default)
            if (args.oldBudget.originalAmount != "") inflateMenu(R.menu.fragment_general_edit_deleteable)

            setOnMenuItemClickListener {
                // Close the keyboard, if it's open
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                when (it.itemId) {
                    R.id.app_bar_delete -> {
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Delete this budget?")
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteOldBudget()
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

        binding = FragmentBudgetEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.currencySpinner.apply {
            inputType = InputType.TYPE_NULL
            setAdapter(
                ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.ccc_currencies_entries,
                    R.layout.dropdown_item_currency
                )
            )
        }
        binding.amountETContainer.apply {
            setEndIconOnClickListener { showQuickBudgetDialog() }
            setErrorIconOnClickListener { showQuickBudgetDialog() }
        }
        return binding.root
    }

    private fun showQuickBudgetDialog() {
        // Close the keyboard, if it's open
        binding.categoryIconFL.requestFocus()
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)

        lifecycleScope.launch {
            AlertDialog.Builder(requireContext())
                .setTitle("Quick Budget")
                .setAdapter(
                    QuickBudgetArrayAdapter(
                        requireContext(),
                        viewModel.generateQuickBudgetItems()
                    )
                ) { _, index -> viewModel.selectedIndex(index) }
                .setOnCancelListener { viewModel.selectedIndex(-1) }
                .create()
                .show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.categoryIconFL.setOnFocusChangeListener { _, focused ->
            if (focused) {
                // Close the keyboard, if it's open
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        viewModel.navigateUpTwoSteps.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.navigateUpHandled()
                findNavController().popBackStack(R.id.budgetDetailFragment, true)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // This callback will only be called when this fragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            isEnabled = true
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