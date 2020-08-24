package com.kaeonx.moneymanager.fragments.transactions

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentTransactionsSearchBinding
import com.kaeonx.moneymanager.handlers.ColourHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "tssearch"

class TransactionsSearchFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsSearchBinding

    private val args: TransactionsSearchFragmentArgs by navArgs()
    private val viewModelFactory by lazy { TransactionsSearchViewModelFactory(args.initSearchQuery) }
    private val viewModel: TransactionsSearchViewModel by viewModels { viewModelFactory }

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
            if (menu.getItem(0).actionView !is SearchView) {
                menu.clear()
                inflateMenu(R.menu.fragment_transactions)
            }

            (menu.getItem(0).actionView as SearchView).apply {
                isIconified = false
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        // Close the keyboard, if it's open
                        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                                as InputMethodManager
                        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                        (menu.getItem(0).actionView as SearchView).clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        if (p0 != null) viewModel.reSearch(p0.trim())
                        return true
                    }
                })
                setQuery(viewModel.currentQuery, false)
                queryHint = "Find in memo"
            }
        }
        binding = FragmentTransactionsSearchBinding.inflate(inflater, container, false)
        binding.transactionsRV.apply {
            setHasFixedSize(true)
            adapter = TransactionsSearchRVAdapter(
                itemOnClickListener = TransactionOnClickListener { view, transactionId ->
                    // Close the keyboard, if it's open
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager
                    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                    if (actionMode == null) {
                        findNavController().run {
                            if (currentDestination?.id == R.id.transactionsSearchFragment) {
                                navigate(
                                    TransactionsSearchFragmentDirections.actionTransactionsSearchFragmentToTransactionEditFragment(
                                        transactionId
                                    )
                                )
                            }
                        }
                    } else {
                        interactWithActionMode(view, transactionId)
                    }
                },
                itemOnLongClickListener = TransactionOnClickListener { view, transaction ->
                    interactWithActionMode(view, transaction)
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.transactionsSearchRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.transactionsRV.adapter as TransactionsSearchRVAdapter).apply {
                submitList2(it)
            }
        }
        binding.transactionsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // Close the keyboard, if it's open
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager
                    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // UP button behaviour
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.setNavigationOnClickListener {
            // Close the keyboard, if it's open
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        if (actionMode != null) actionMode!!.finish()
        super.onDestroyView()
    }

}