package com.kaeonx.moneymanager.fragments.budget

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentBudgetsBinding
import com.kaeonx.moneymanager.fragments.transactions.MYPickerDialog
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.util.*

class BudgetsFragment : Fragment() {

    private lateinit var binding: FragmentBudgetsBinding

    private val args: BudgetsFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        BudgetsViewModelFactory(
            args.initCalendar ?: CalendarHandler.getStartOfMonthCalendar(Calendar.getInstance())
        )
    }
    private val viewModel: BudgetsViewModel by viewModels { viewModelFactory }

    private val selectedViews by lazy { arrayListOf<View>() }
    private val listOfCategoriesSelected by lazy { arrayListOf<String>() }
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
                            .setMessage(getString(R.string.action_mode_batch_delete_budgets))
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteBudgets(listOfCategoriesSelected.toList())
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
                listOfCategoriesSelected.clear()
            }
        }
    }

    private fun interactWithActionMode(view: View, category: String) {
        fun add(view: View, _category: String) {
            toggleView(view, true)
            selectedViews.add(view)
            listOfCategoriesSelected.add(_category)
        }

        fun remove(view: View, _category: String) {
            selectedViews.remove(view)
            toggleView(view, false)
            listOfCategoriesSelected.remove(_category)
        }

        if (actionMode == null) {
            // Start the CAB using the ActionMode.Callback defined above
            actionMode = requireActivity().startActionMode(actionModeCallback)
            add(view, category)
        } else {
            if (category in listOfCategoriesSelected) {
                if (listOfCategoriesSelected.size == 1) {
                    actionMode?.finish()
                } else {
                    // Remove
                    remove(view, category)
                }
            } else {
                // Add
                add(view, category)
            }
        }
        actionMode?.title = "${listOfCategoriesSelected.size} selected"
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
            inflateMenu(R.menu.fragment_general_select_month)
            lifecycleScope.launch(Dispatchers.Main) {
                while (viewModel.addOptions == null) {
                    ensureActive()
                    delay(1L)
                }
                if (viewModel.addOptions!!.isNotEmpty()) {
                    menu.clear()
                    inflateMenu(R.menu.fragment_budgets)
                }
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_add_budget -> {
                        // if you can click the plus icon, means options is non null.
                        // Show dialog to choose the category to add
                        AlertDialog.Builder(requireContext())
                            .setTitle("Create budget for:")
                            .setItems(viewModel.addOptions!!) { _, indexSelected ->
                                binding.root.findNavController().run {
                                    if (currentDestination?.id == R.id.budgetsFragment) {
                                        navigate(
                                            BudgetsFragmentDirections.actionBudgetsFragmentToBudgetEditFragment(
                                                Budget(
                                                    category = viewModel.addOptions!![indexSelected].toString(),
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
                    R.id.menu_select_month -> {
                        MYPickerDialog.createDialog(
                            context = requireContext(),
                            initCalendar = viewModel.displayCalendar.value!!,
                            resultListener = MYPickerDialog.MYPickerDialogListener { result ->
                                viewModel.selectMonth(result[0], result[1])
                            }

                        ).show()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }

        binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        binding.budgetRV.apply {
            setHasFixedSize(true)
            adapter = BudgetsRVAdapter(
                itemOnClickListener = BudgetOnClickListener { view, category ->
                    if (actionMode == null) {
                        findNavController().run {
                            if (currentDestination?.id == R.id.budgetsFragment) {
                                navigate(
                                    BudgetsFragmentDirections.actionBudgetsFragmentToBudgetDetailFragment(
                                        category,
                                        viewModel.displayCalendar.value!!.clone() as Calendar
                                    )
                                )
                            }
                        }
                    } else {
                        interactWithActionMode(view, category)
                    }
                },
                itemOnLongClickListener = BudgetOnClickListener { view, category ->
                    interactWithActionMode(view, category)
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
    }

}