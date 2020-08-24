package com.kaeonx.moneymanager.fragments.detail

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentDetailCategoryBinding
import com.kaeonx.moneymanager.fragments.transactions.MYPickerDialog
import com.kaeonx.moneymanager.fragments.transactions.TransactionOnClickListener
import com.kaeonx.moneymanager.handlers.ColourHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailCategoryFragment : Fragment() {

    private lateinit var binding: FragmentDetailCategoryBinding

    internal val args: DetailCategoryFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        DetailCategoryViewModelFactory(
            args.initIsYearMode,
            args.initArchiveCalendarStart,
            args.type,
            args.category,
            args.initCalendar
        )
    }
    private val viewModel: DetailCategoryViewModel by viewModels { viewModelFactory }

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

    private fun interactWithActionMode(view: View, transactionId: Int) {
        fun add(view: View, _transactionId: Int) {
            toggleView(view, true)
            selectedViews.add(view)
            listOfIdsSelected.add(_transactionId)
        }

        fun remove(view: View, _transactionId: Int) {
            selectedViews.remove(view)
            toggleView(view, false)
            listOfIdsSelected.remove(_transactionId)
        }

        if (actionMode == null) {
            // Start the CAB using the ActionMode.Callback defined above
            actionMode = requireActivity().startActionMode(actionModeCallback)
            add(view, transactionId)
        } else {
            if (transactionId in listOfIdsSelected) {
                if (listOfIdsSelected.size == 1) {
                    actionMode?.finish()
                } else {
                    // Remove
                    remove(view, transactionId)
                }
            } else {
                // Add
                add(view, transactionId)
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
            title = "${args.type}: ${args.category}"
            menu.clear()
            inflateMenu(R.menu.fragment_general_select_month_with_toggle_view)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_toggle_view -> {
                        // This menu button isn't visible if args.yearModeEnabled is false.
                        viewModel.toggleView()
                        true
                    }
                    R.id.menu_select_month -> {
                        MYPickerDialog.createDialog(
                            context = requireContext(),
                            initCalendar = viewModel.displayCalendarStart.value!!,
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

        binding = FragmentDetailCategoryBinding.inflate(inflater, container, false)
        binding.detailCategoryRV.apply {
            setHasFixedSize(true)
            adapter = DetailCategoryRVAdapter(
                itemOnClickListener = TransactionOnClickListener { view, transactionId ->
                    if (actionMode == null) {
                        findNavController().run {
                            if (currentDestination?.id == R.id.detailCategoryFragment) {
                                navigate(
                                    DetailCategoryFragmentDirections.actionDetailCategoryFragmentToTransactionEditFragment(
                                        transactionId
                                    )
                                )
                            }
                        }
                    } else {
                        interactWithActionMode(view, transactionId)
                    }
                },
                itemOnLongClickListener = TransactionOnClickListener { view, transactionId ->
                    interactWithActionMode(view, transactionId)
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.categoryTypeRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.detailCategoryRV.adapter as DetailCategoryRVAdapter).apply {
//                submitList(null)
                submitList2(it)
            }
        }
    }

    override fun onDestroyView() {
        if (actionMode != null) actionMode!!.finish()
        super.onDestroyView()
    }

}