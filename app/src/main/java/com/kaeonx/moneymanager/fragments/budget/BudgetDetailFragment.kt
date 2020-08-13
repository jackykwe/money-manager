package com.kaeonx.moneymanager.fragments.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentBudgetDetailBinding
import com.kaeonx.moneymanager.fragments.transactions.MYPickerDialog
import java.util.*

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
                            navigate(
                                BudgetDetailFragmentDirections.actionBudgetDetailFragmentToDetailCategoryFragment(
                                    yearModeEnabled = false,
                                    initIsYearMode = false,
                                    initArchiveCalendarStart = viewModel.displayCalendar.value!!.clone() as Calendar,  // will not be used, but just passing it in.
                                    type = "Expenses",
                                    category = args.category,
                                    initCalendar = viewModel.displayCalendar.value!!.clone() as Calendar
                                )
                            )
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            inflateMenu(R.menu.fragment_general_select_month)
            setOnMenuItemClickListener {
                when (it.itemId) {
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
    }

}