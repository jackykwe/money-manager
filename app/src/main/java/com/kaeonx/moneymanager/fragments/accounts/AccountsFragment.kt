package com.kaeonx.moneymanager.fragments.accounts

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentAccountsBinding
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Account
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val ACC_PICKER_EDITABLE = "editable"
private const val TAG = "accfrag"

// Essentially the same code as AccountsDF, except for the childFragment arguments.
class AccountsFragment : Fragment() {

    private lateinit var binding: FragmentAccountsBinding
    private val viewModel: AccountsViewModel by viewModels()

    private var numberOfSavedAccounts = 1

    private val selectedViews by lazy { arrayListOf<View>() }
    private val listOfIdsSelected by lazy { arrayListOf<Int>() }
    private var actionMode: ActionMode? = null
    private val actionModeCallback by lazy {
        object : ActionMode.Callback {
            // Called when the action mode is created; startActionMode() was called
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate a menu resource providing context menu items
                mode.menuInflater.inflate(R.menu.fragment_general_edit_deleteable, menu)
                (requireActivity() as MainActivity).binding.rootDL.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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
                            .setTitle(getString(R.string.action_mode_batch_delete_accounts_title))
                            .setMessage(getString(R.string.action_mode_batch_delete_accounts_message))
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteAccounts(listOfIdsSelected.toList())
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
                (requireActivity() as MainActivity).binding.rootDL.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    private fun interactWithActionMode(view: View, account: Account) {
        fun add(view: View, _account: Account) {
            toggleView(view, true)
            selectedViews.add(view)
            listOfIdsSelected.add(_account.accountId!!)
        }

        fun remove(view: View, _account: Account) {
            selectedViews.remove(view)
            toggleView(view, false)
            listOfIdsSelected.remove(_account.accountId!!)
        }

        if (actionMode == null) {
            // Start the CAB using the ActionMode.Callback defined above
            if (listOfIdsSelected.size + 1 < numberOfSavedAccounts) {
                actionMode = requireActivity().startActionMode(actionModeCallback)
                // Add
                add(view, account)
            }
        } else {
            if (account.accountId!! in listOfIdsSelected) {
                if (listOfIdsSelected.size == 1) {
                    actionMode?.finish()
                } else {
                    // Remove
                    remove(view, account)
                }
            } else {
                if (listOfIdsSelected.size + 1 < numberOfSavedAccounts) {
                    // Add
                    add(view, account)
                } else {
                    Snackbar.make(
                        binding.root,
                        "You must have at least one account",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
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

    internal val itemOnClickListener by lazy {
        AccountOnClickListener { view, account ->
            if (actionMode == null) {
                val cond1 = UserRepository.getInstance().accounts.value!!.size > 1
                val cond2 = account.name != "Add…"
                findNavController().run {
                    if (currentDestination?.id == R.id.accountsFragment) {
                        navigate(
                            AccountsFragmentDirections.actionAccountsFragmentToAccountEditFragment(
                                account,
                                cond1 && cond2
                            )
                        )
                    }
                }
            } else {
                if (account.accountId != null) interactWithActionMode(view, account)
            }
        }
    }

    internal val itemOnLongClickListener by lazy {
        AccountOnClickListener { view, account ->
            if (account.accountId != null) interactWithActionMode(view, account)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentAccountsBinding.inflate(inflater, container, false)

        val childFragment = AccountsDisplayFragment()
        childFragment.arguments = Bundle().apply {
            putBoolean(ACC_PICKER_EDITABLE, true)
        }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.childFragmentContainer, childFragment)
            .commit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.accountSize.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            numberOfSavedAccounts = it
        }
    }
}
