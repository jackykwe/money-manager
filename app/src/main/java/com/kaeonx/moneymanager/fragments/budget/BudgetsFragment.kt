package com.kaeonx.moneymanager.fragments.budget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentBudgetsBinding
import com.kaeonx.moneymanager.databinding.LlItemBudgetBinding
import com.kaeonx.moneymanager.databinding.LlItemDetailTypeNoDataBinding
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.Budget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "bdgfm"

class BudgetsFragment : Fragment() {

    private lateinit var binding: FragmentBudgetsBinding

    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.budgetLL.addView(
            LlItemDetailTypeNoDataBinding.inflate(
                layoutInflater,
                null,
                false
            ).root
        )

        viewModel.budgets.observe(viewLifecycleOwner) { list ->
            Log.d(TAG, "Observing budgets LD!")
            binding.budgetLL.apply {
                removeAllViews()
                for (budget in list) {
                    val itemBinding = LlItemBudgetBinding.inflate(layoutInflater, null, false)
//                    itemBinding.budget = budget
//                    itemBinding.onClickListener = BudgetOnClickListener {
//                        Toast.makeText(requireContext(), "Wow, you want $it?", Toast.LENGTH_SHORT).show()
//                    }
//                    itemBinding.executePendingBindings()
                    addView(itemBinding.root)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Don't inflate (or disable) if all available budgets have been created
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            lifecycleScope.launch {
                while (viewModel.addOptions == null) {
                    delay(1L)
                    Log.d(TAG, "wow still null")
                }
                if (viewModel.addOptions!!.isNotEmpty()) inflateMenu(R.menu.fragment_budgets)
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_add_budget -> {
                        val options =
                            viewModel.addOptions!!  // if you can click the plus icon, means options is non null.
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
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }
}