package com.kaeonx.moneymanager.fragments.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentTransactionsBinding
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import java.util.*

private const val TAG = "transactionFrag"

// TODO: navigate back to current day / month (when month selector is active)

class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private val viewModel: TransactionsFragmentViewModel by viewModels()

    private val savedStateHandle by lazy { findNavController().getBackStackEntry(R.id.transactionsFragment).savedStateHandle }

    private var isExpanded = false
    private var firstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        binding.transactionsRV.adapter =
            TransactionsRVAdapter(
                TransactionOnClickListener { transaction ->
                    findNavController().navigate(
                        TransactionsFragmentDirections.actionTransactionsFragmentToTransactionEditFragment(
                            transaction.transactionId!!
                        )
                    )
                },
                GenericOnClickListener { viewModel.monthMinusOne() },
                GenericOnClickListener {
                    Toast.makeText(requireContext(), "Wow you want me? 2", Toast.LENGTH_SHORT)
                        .show()
                },
                GenericOnClickListener { viewModel.monthPlusOne() }
            )

        viewModel.sensitiveDayTransactions.observe(viewLifecycleOwner) {
            (binding.transactionsRV.adapter as TransactionsRVAdapter).submitListAndAddHeaders(
                CalendarHandler.getFormattedString(viewModel.displayCalendar.value!!, "MMM yyyy")
                    .toUpperCase(Locale.ROOT),
                it
            )
        }

        return binding.root
    }

    /*
        tcttlLeftArrowBT.setOnClickListener { firebaseViewModel.yearMinusOne() }
        tcttlRightArrowBT.setOnClickListener { firebaseViewModel.yearPlusOne() }
        tcttlYearBT.setOnClickListener {
            YearPickerDialogFragment().show(childFragmentManager, "yearPicker")
        }
    }
     */

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup of Toolbar
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            inflateMenu(R.menu.fragment_transactions)
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

        // Setup of FAB
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityFAB.setOnClickListener {
            findNavController().navigate(
                TransactionsFragmentDirections.actionTransactionsFragmentToTransactionsBSDF(
                    Transaction(
                        transactionId = null,
                        timestamp = 0L,
                        type = "Expenses",
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