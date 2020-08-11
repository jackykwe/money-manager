package com.kaeonx.moneymanager.fragments.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentBudgetDetailBinding
import com.kaeonx.moneymanager.fragments.transactions.MY_PICKER_RESULT

class BudgetDetailFragment : Fragment() {

    private lateinit var binding: FragmentBudgetDetailBinding

    private val args: BudgetDetailFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        BudgetDetailViewModelFactory(
            args.category,
            args.initCalendar
        )
    }
    private val viewModel: BudgetDetailViewModel by viewModels { viewModelFactory }

    private val savedStateHandle by lazy { findNavController().getBackStackEntry(R.id.budgetDetailFragment).savedStateHandle }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.title =
            "Budget: ${args.category}"
        binding = FragmentBudgetDetailBinding.inflate(inflater, container, false)
        binding.budgetDetailRV.apply {
            setHasFixedSize(true)
            adapter = BudgetDetailRVAdapter(
                expensesOnClickListener = BudgetDetailOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.budgetDetailFragment) {
                            Unit
                        }
                    }
                },
                editOnClickListener = BudgetDetailOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.budgetDetailFragment) {
                            navigate(
                                BudgetDetailFragmentDirections.actionBudgetDetailFragmentToBudgetEditFragment(
                                    viewModel.budget.value!!
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
        viewModel.budgetDetailRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.budgetDetailRV.adapter as BudgetDetailRVAdapter).apply {
                submitList2(it)
            }
        }

        savedStateHandle.getLiveData<Array<Int>?>(MY_PICKER_RESULT).observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                viewModel.selectMonth(it[0], it[1])
                savedStateHandle.set(MY_PICKER_RESULT, null)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_select_month -> {
                        binding.root.findNavController().run {
                            if (currentDestination?.id == R.id.budgetDetailFragment) {
                                navigate(
                                    BudgetDetailFragmentDirections.actionBudgetDetailFragmentToMonthYearPickerDialogFragment(
                                        R.id.budgetDetailFragment,
                                        viewModel.displayCalendar.value!!  // no need clone, since no edits will be made to it
                                    )
                                )
                            }
                        }
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }

}