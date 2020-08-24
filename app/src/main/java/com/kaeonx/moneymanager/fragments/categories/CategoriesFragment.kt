package com.kaeonx.moneymanager.fragments.categories

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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentCategoriesBinding
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "categoriesFrag"

// Essentially the same code as CategoriesDF, except for the adapter.
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel: CategoriesViewModel by viewModels()

    private var numberOfSavedCategories = Pair(1, 1)  // Income, Expenses
    private fun getCurrentPageMax(): Int {
        return when (val position = binding.catPickerTL.selectedTabPosition) {
            0 -> numberOfSavedCategories.first
            1 -> numberOfSavedCategories.second
            else -> throw IllegalStateException("Unknown tab position $position")
        }
    }

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
                            .setTitle(getString(R.string.action_mode_batch_delete_categories_title))
                            .setMessage(getString(R.string.action_mode_batch_delete_categories_message))
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteCategories(listOfIdsSelected.toList())
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

    private fun interactWithActionMode(view: View, category: Category) {
        fun add(view: View, _category: Category) {
            toggleView(view, true)
            selectedViews.add(view)
            listOfIdsSelected.add(_category.categoryId!!)
        }

        fun remove(view: View, _category: Category) {
            selectedViews.remove(view)
            toggleView(view, false)
            listOfIdsSelected.remove(_category.categoryId!!)
        }

        if (actionMode == null) {
            // Start the CAB using the ActionMode.Callback defined above
            if (listOfIdsSelected.size + 1 < getCurrentPageMax()) {
                actionMode = requireActivity().startActionMode(actionModeCallback)
                add(view, category)
            }
        } else {
            if (category.categoryId!! in listOfIdsSelected) {
                if (listOfIdsSelected.size == 1) {
                    actionMode?.finish()
                } else {
                    // Remove
                    remove(view, category)
                }
            } else {
                if (listOfIdsSelected.size + 1 < getCurrentPageMax()) {
                    // Add
                    add(view, category)
                } else {
                    val name = when (val position = binding.catPickerTL.selectedTabPosition) {
                        0 -> "Income"
                        1 -> "Expenses"
                        else -> throw IllegalArgumentException("Unknown tab position ($position) reached. Position should be 0 or 1 only.")
                    }
                    Snackbar.make(
                        binding.root,
                        "You must have at least one $name category",
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.catPickerVP.offscreenPageLimit = 1
        binding.catPickerVP.adapter =
            TypeDisplayFragmentStateAdapter(
                parentFragment = this,
                editable = true,
                itemOnClickListener = CategoryOnClickListener { view, category ->
                    if (actionMode == null) {
                        val cond1 = when (val type = category.type) {
                            "Income" -> UserRepository.getInstance().categories.value!!.count { it.type == "Income" } > 1
                            "Expenses" -> UserRepository.getInstance().categories.value!!.count { it.type == "Expenses" } > 1
                            else -> throw IllegalArgumentException("Unknown type $type")
                        }
                        val cond2 = category.name != "Add…"
                        findNavController().run {
                            if (currentDestination?.id == R.id.categoriesFragment) {
                                navigate(
                                    CategoriesFragmentDirections.actionCategoriesFragmentToCategoryEditFragment(
                                        oldCategory = category,
                                        deletable = cond1 && cond2
                                    )
                                )
                            }
                        }
                    } else {
                        if (category.categoryId != null) interactWithActionMode(view, category)
                    }
                },
                itemOnLongClickListener = CategoryOnClickListener { view, category ->
                    if (category.categoryId != null) interactWithActionMode(view, category)
                }
            )
        return binding.root
    }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (actionMode != null) actionMode!!.finish()
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            Unit
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            Unit
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        TabLayoutMediator(binding.catPickerTL, binding.catPickerVP) { tab, position ->
            tab.text = when (position) {
                0 -> "Income"
                1 -> "Expenses"
                else -> throw IllegalArgumentException("Unknown tab position ($position) reached. Position should be 0 or 1 only.")
            }
        }.attach()

        binding.catPickerTL.addOnTabSelectedListener(tabSelectedListener)

        viewModel.categoriesSize.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            numberOfSavedCategories = it
        }
    }

    override fun onDestroyView() {
        if (actionMode != null) actionMode!!.finish()
        binding.catPickerTL.removeOnTabSelectedListener(tabSelectedListener)
        super.onDestroyView()
    }

}