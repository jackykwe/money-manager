package com.kaeonx.moneymanager.fragments.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentBudgetsBinding
import com.kaeonx.moneymanager.fragments.transactions.MY_PICKER_RESULT
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.Budget
import java.util.*

private const val TAG = "bdgfm"

class BudgetsFragment : Fragment() {

    private lateinit var binding: FragmentBudgetsBinding

    private val args: BudgetsFragmentArgs by navArgs()
    private val viewModelFactory by lazy { BudgetsViewModelFactory(args.initCalendar) }
    private val viewModel: BudgetsViewModel by viewModels { viewModelFactory }

    private val savedStateHandle by lazy { findNavController().getBackStackEntry(R.id.budgetsFragment).savedStateHandle }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        binding.budgetRV.apply {
            setHasFixedSize(true)
            adapter = BudgetsRVAdapter(
                BudgetOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.budgetsFragment) {
                            navigate(
                                BudgetsFragmentDirections.actionBudgetsFragmentToBudgetDetailFragment(
                                    it,
                                    viewModel.displayCalendar.value!!.clone() as Calendar
                                )
                            )
                        }
                    }
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.budgetsRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.budgetRV.adapter as BudgetsRVAdapter).apply {
                submitList2(it)
            }
        }

        savedStateHandle.getLiveData<Array<Int>?>(MY_PICKER_RESULT).observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                viewModel.selectMonth(it[0], it[1])
                savedStateHandle.set(MY_PICKER_RESULT, null)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_add_budget -> {
                        // if you can click the plus icon, means options is non null.
                        val options = viewModel.addOptions!!
                        return@setOnMenuItemClickListener if (options.isEmpty()) {
                            Snackbar.make(
                                binding.root,
                                "All categories already have a budget",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            true
                        } else {
                            // Show dialog to choose the category to add
                            AlertDialog.Builder(requireContext())
                                .setTitle("Create budget for:")
                                .setItems(options) { _, indexSelected ->
                                    binding.root.findNavController().run {
                                        if (currentDestination?.id == R.id.budgetsFragment) {
                                            navigate(
                                                BudgetsFragmentDirections.actionBudgetsFragmentToBudgetEditFragment(
                                                    Budget(
                                                        category = options[indexSelected].toString(),
                                                        originalCurrency = UserPDS.getString("ccc_home_currency"),
                                                        originalAmount = ""
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }
                                .create()
                                .show()
                            true
                        }
                    }
                    R.id.menu_select_month -> {
                        binding.root.findNavController().run {
                            if (currentDestination?.id == R.id.budgetsFragment) {
                                navigate(
                                    BudgetsFragmentDirections.actionBudgetsFragmentToMonthYearPickerDialogFragment(
                                        R.id.budgetsFragment,
                                        viewModel.displayCalendar.value!!  // no need clone, since no edits will be made to it
                                    )
                                )
                            }
                        }
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }
}