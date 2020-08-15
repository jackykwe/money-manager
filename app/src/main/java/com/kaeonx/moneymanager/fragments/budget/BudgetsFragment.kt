package com.kaeonx.moneymanager.fragments.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        binding.budgetRV.apply {
            setHasFixedSize(true)
            adapter = BudgetsRVAdapter(
                BudgetOnClickListener {
                    findNavController().run {
                        if (currentDestination?.id == R.id.budgetsFragment) {
                            navigate(
                                BudgetsFragmentDirections.actionBudgetsFragmentToBudgetDetailFragment(
                                    it,
                                    viewModel.displayCalendar.value!!.clone() as Calendar
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
        viewModel.budgetsRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.budgetRV.adapter as BudgetsRVAdapter).apply {
                submitList2(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                while (viewModel.addOptions == null) {
                    ensureActive()
                    delay(1L)
                }
                if (viewModel.addOptions!!.isEmpty()) {
                    inflateMenu(R.menu.fragment_general_select_month)
                } else {
                    inflateMenu(R.menu.fragment_budgets)
                }
                MainActivity.styleMenuIcons(menu)
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
    }
}