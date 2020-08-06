package com.kaeonx.moneymanager.fragments.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.FragmentTransactionsBinding
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "transactionFrag"

// TODO: navigate back to current day / month (when month selector is active)

class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private val viewModel: TransactionsFragmentViewModel by viewModels()

    private val savedStateHandle by lazy { findNavController().getBackStackEntry(R.id.transactionsFragment).savedStateHandle }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Setup of Toolbar
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_sync -> {
                        Snackbar.make(requireView(), "Fragment!", Snackbar.LENGTH_SHORT).show()
//                        Toast.makeText(requireContext(), "Fragment!", Toast.LENGTH_LONG).show()
                        true
                    }
                    else -> false
                }
            }
        }

        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        binding.transactionsRV.apply {
            setHasFixedSize(true)  // an optimisation, clarified by https://stackoverflow.com/a/39736376/7254995
            adapter = TransactionsRVAdapter(
                itemOnClickListener = TransactionOnClickListener { transaction ->
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToTransactionEditFragment(
                                    transaction.transactionId!!
                                )
                            )
                        }
                    }
                },
                headerLeftArrowClickListener = GenericOnClickListener { viewModel.monthMinusOne() },
                headerMonthClickListener = GenericOnClickListener {
                    // Courtesy of https://stackoverflow.com/a/53737537/7254995
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToMonthYearPickerDialogFragment(
                                    viewModel.displayCalendar.value!!  // no need clone, since no edits will be made to it
                                )
                            )
                        }
                    }
                },
                headerRightArrowClickListener = GenericOnClickListener { viewModel.monthPlusOne() },
                summaryBudgetClickListener = GenericOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(TransactionsFragmentDirections.actionTransactionsFragmentToBudgetFragment())
                        }
                    }
                },
                summaryIncomeClickListener = GenericOnClickListener { Unit },
                summaryExpensesClickListener = GenericOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToTypeDetailFragment(
                                    "Expenses",
                                    viewModel.displayCalendar.value!!.clone() as Calendar,
                                    true // todo: viewModel to have a var with private set
                                )
                            )
                        }
                    }
                },
                summaryPieChartClickListener = GenericOnClickListener {
                    Snackbar.make(
                        binding.root,
                        "Navigate to PieChartHelpFragment",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            )
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.sensitiveDayTransactions.observe(viewLifecycleOwner) {
            if (it == null) return@observe  // prevents false firing when ViewModel is initialised
            lifecycleScope.launch(Dispatchers.Default) {
                (binding.transactionsRV.adapter as TransactionsRVAdapter).apply {
                    submitList(null)
                    submitListAndAddHeaders(
                        it,
                        CalendarHandler.getFormattedString(
                            viewModel.displayCalendar.value!!.clone() as Calendar,
                            "MMM yyyy"
                        )
                            .toUpperCase(Locale.ROOT),
                        viewModel.getSummaryData(it)
                    )
                }
            }
        }

        savedStateHandle.getLiveData<Array<Int>?>(MY_PICKER_RESULT).observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                viewModel.updateMonthYear(it[0], it[1])
                savedStateHandle.set(MY_PICKER_RESULT, null)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup of FAB
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityFAB.setOnClickListener {
            findNavController().run {
                if (currentDestination?.id == R.id.transactionsFragment) {
                    navigate(
                        TransactionsFragmentDirections.actionTransactionsFragmentToTransactionsBSDF(
                            Transaction(
                                transactionId = null,
                                timestamp = 0L,
                                type = "?",
                                category = "?",
                                account = "Cash",
                                memo = "",
                                originalCurrency = "SGD",
                                originalAmount = "0"
                            )
                        )
                    )
                }
            }
        }
    }

}

////            // Courtesy of https://medium.com/androiddevelopers/appcompat-v23-2-daynight-d10f90c83e94
////            val newMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
////                Configuration.UI_MODE_NIGHT_NO -> {
////                    // Night mode is not active, we're in day time
////                    AppCompatDelegate.MODE_NIGHT_YES
////                }
////                Configuration.UI_MODE_NIGHT_YES -> {
////                    // Night mode is active, we're at night!
////                    AppCompatDelegate.MODE_NIGHT_NO
////                }
////                else -> {
////                    throw Exception("OI")
////                }
////            }
////            Toast.makeText(requireContext(), "Switching to mode $newMode", Toast.LENGTH_LONG).show()
////            AppCompatDelegate.setDefaultNightMode(newMode)