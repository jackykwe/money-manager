package com.kaeonx.moneymanager.fragments.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentDetailCategoryBinding
import com.kaeonx.moneymanager.fragments.transactions.MYPickerDialog

class DetailCategoryFragment : Fragment() {

    private lateinit var binding: FragmentDetailCategoryBinding

    internal val args: DetailCategoryFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        DetailCategoryViewModelFactory(
            args.yearModeEnabled,
            args.initIsYearMode,
            args.initArchiveCalendarStart,
            args.type,
            args.category,
            args.initCalendar
        )
    }
    private val viewModel: DetailCategoryViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.title =
            "${args.type}: ${args.category}"
        binding = FragmentDetailCategoryBinding.inflate(inflater, container, false)
        binding.detailCategoryRV.apply {
            setHasFixedSize(true)
            adapter = DetailCategoryRVAdapter(
                DetailCategoryOnClickListener { transactionId ->
                    findNavController().run {
                        if (currentDestination?.id == R.id.detailCategoryFragment) {
                            navigate(
                                DetailCategoryFragmentDirections.actionDetailCategoryFragmentToTransactionEditFragment(
                                    transactionId
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
        viewModel.categoryTypeRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.detailCategoryRV.adapter as DetailCategoryRVAdapter).apply {
//                submitList(null)
                submitList2(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            if (args.yearModeEnabled) {
                inflateMenu(R.menu.fragment_general_select_month_with_toggle_view)
            } else {
                inflateMenu(R.menu.fragment_general_select_month)
            }
            MainActivity.styleMenuIcons(menu)

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
    }
}