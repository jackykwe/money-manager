package com.kaeonx.moneymanager.fragments.transactions

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.FragmentTransactionsBinding
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private val viewModel: TransactionsViewModel by viewModels()

    // TODO future: Make this into a generic class? Instantiate it in each fragment as desired
    private val selectedViews by lazy { arrayListOf<View>() }
    private val listOfIdsSelected by lazy { arrayListOf<Int>() }
    private var actionMode: ActionMode? = null
    private val actionModeCallback by lazy {
        object : ActionMode.Callback {
            // Called when the action mode is created; startActionMode() was called
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate a menu resource providing context menu items
                mode.menuInflater.inflate(R.menu.fragment_general_edit_deleteable, menu)
                return true
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false // Return false if nothing is done
            }

            // Called when the user selects a contextual menu item
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.app_bar_delete -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage(getString(R.string.action_mode_batch_delete_transactions))
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteTransactions(listOfIdsSelected.toList())
                                mode.finish() // Action picked, so close the CAB
                            }
                            .setNegativeButton(R.string.cancel) { _, _ -> }
                            .create()
                            .show()
                        true
                    }
                    else -> false
                }
            }

            // Called when the user exits the action mode
            override fun onDestroyActionMode(mode: ActionMode) {
                actionMode = null
                selectedViews.run {
                    forEach { toggleView(it, false) }
                    clear()
                }
                listOfIdsSelected.clear()
            }
        }
    }

    private fun interactWithActionMode(view: View, transaction: Transaction) {
        fun add(view: View, transaction: Transaction) {
            toggleView(view, true)
            selectedViews.add(view)
            listOfIdsSelected.add(transaction.transactionId!!)
        }

        fun remove(view: View, transaction: Transaction) {
            selectedViews.remove(view)
            toggleView(view, false)
            listOfIdsSelected.remove(transaction.transactionId!!)
        }

        if (actionMode == null) {
            // Start the CAB using the ActionMode.Callback defined above
            actionMode = requireActivity().startActionMode(actionModeCallback)
            add(view, transaction)
        } else {
            if (transaction.transactionId!! in listOfIdsSelected) {
                if (listOfIdsSelected.size == 1) {
                    actionMode?.finish()
                } else {
                    // Remove
                    remove(view, transaction)
                }
            } else {
                // Add
                add(view, transaction)
            }
        }
        actionMode?.title = "${listOfIdsSelected.size} selected"
    }

    private fun toggleView(view: View, newIsSelected: Boolean) {
        lifecycleScope.launch {
            view.backgroundTintList = ColourHandler.getColourStateListThemedOf(
                if (newIsSelected) "Grey" else "TRANSPARENT"
            )
            delay(500L)
            view.foreground = if (newIsSelected) null else with(TypedValue()) {
                requireContext().theme.resolveAttribute(
                    android.R.attr.selectableItemBackground,
                    this,
                    true
                )
                ContextCompat.getDrawable(requireContext(), this.resourceId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            menu.clear()
            inflateMenu(R.menu.fragment_transactions)
        }

        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        binding.transactionsRV.apply {
            setHasFixedSize(true)  // an optimisation, clarified by https://stackoverflow.com/a/39736376/7254995
            adapter = TransactionsRVAdapter(
                itemOnClickListener = TransactionOnClickListener { view, transaction ->
                    if (actionMode == null) {
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
                    } else {
                        interactWithActionMode(view, transaction)
                    }
                },
                itemOnLongClickListener = TransactionOnClickListener { view, transaction ->
                    interactWithActionMode(view, transaction)
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
                summaryIncomeClickListener = GenericOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToDetailTypeFragment(
                                    initType = "Income",
                                    initCalendar = viewModel.displayCalendar.value!!.clone() as Calendar
                                )
                            )
                        }
                    }
                },
                summaryExpensesClickListener = GenericOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToDetailTypeFragment(
                                    initType = "Expenses",
                                    initCalendar = viewModel.displayCalendar.value!!.clone() as Calendar
                                )
                            )
                        }
                    }
                },
                summaryPieChartClickListener = GenericOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsFragment) {
                            navigate(
                                TransactionsFragmentDirections.actionTransactionsFragmentToBudgetDetailFragment(
                                    initCalendar = viewModel.displayCalendar.value!!.clone() as Calendar,
                                    category = "Overall"
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
        viewModel.transactionsRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe  // prevents false firing when ViewModel is initialised
            (binding.transactionsRV.adapter as TransactionsRVAdapter).apply {
//                submitList(null)
                submitList2(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            (menu.getItem(0).actionView as SearchView).apply {
                queryHint = "Find in memo"
                setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(p0: String?): Boolean = true
                        override fun onQueryTextChange(p0: String?): Boolean {
                            p0?.let { query ->
                                if (query.isEmpty()) return@let
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