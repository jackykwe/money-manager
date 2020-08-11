package com.kaeonx.moneymanager.fragments.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "transactionFrag"

class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private val viewModel: TransactionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        binding.transactionsRV.apply {
            setHasFixedSize(true)  // an optimisation, clarified by https://stackoverflow.com/a/39736376/7254995
            adapter = TransactionsRVAdapter(
                itemOnClickListener = TransactionOnClickListener { transaction ->
                    findNavController().run {
                        // Courtesy of https://stackoverflow.com/a/53737537/7254995
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
                    MYPickerDialog.createDialog(
                        context = requireContext(),
                        initCalendar = viewModel.displayCalendar.value!!,
                        resultListener = MYPickerDialog.MYPickerDialogListener { result ->
                            viewModel.updateMonthYear(result[0], result[1])
                        }
                    ).show()
                },
                headerRightArrowClickListener = GenericOnClickListener { viewModel.monthPlusOne() },
                summaryBudgetClickListener = GenericOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToBudgetsFragment(
                                    initCalendar = viewModel.displayCalendar.value!!.clone() as Calendar
                                )
                            )
                        }
                    }
                },
                summaryIncomeClickListener = GenericOnClickListener { Unit },
                summaryExpensesClickListener = GenericOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToDetailTypeFragment(
                                    initType = "Expenses",  // todo: When there are more than one possible summary setting, peg to that.
                                    initCalendar = viewModel.displayCalendar.value!!.clone() as Calendar
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            (menu.getItem(0).actionView as SearchView).setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean = true
                    override fun onQueryTextChange(p0: String?): Boolean {
                        p0?.let { query ->
                            findNavController().run {
                                if (currentDestination?.id == R.id.transactionsFragment) {
                                    navigate(
                                        TransactionsFragmentDirections.actionTransactionsFragmentToTransactionsSearchFragment(
                                            query
                                        )
                                    )
                                }
                            }
                        }
                        return true
                    }
                }
            )
        }

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
                                account = UserPDS.getString("tst_default_account"),
                                memo = "",
                                originalCurrency = UserPDS.getString("ccc_home_currency"),
                                originalAmount = "0"
                            )
                        )
                    )
                }
            }
        }
    }
}