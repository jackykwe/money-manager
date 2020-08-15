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
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.FragmentDetailTypeBinding
import com.kaeonx.moneymanager.fragments.transactions.MYPickerDialog
import java.util.*

class DetailTypeFragment : Fragment() {

    private lateinit var binding: FragmentDetailTypeBinding

    private val args: DetailTypeFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        DetailTypeViewModelFactory(
            args.initType,
            args.initCalendar
        )
    }
    private val viewModel: DetailTypeViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            title = args.initType
            menu.clear()
            inflateMenu(R.menu.fragment_general_select_month_with_toggle_view)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_toggle_view -> {
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

        binding = FragmentDetailTypeBinding.inflate(inflater, container, false)
        binding.detailTypeRV.apply {
            setHasFixedSize(true)
            adapter = DetailTypeRVAdapter(
                DetailTypeOnClickListener { type, category ->
                    findNavController().run {
                        if (currentDestination?.id == R.id.detailTypeFragment) {
                            navigate(
                                DetailTypeFragmentDirections.actionDetailTypeFragmentToDetailCategoryFragment(
                                    initIsYearMode = viewModel.isYearMode,
                                    initArchiveCalendarStart = viewModel.archiveCalendarStart.clone() as Calendar,
                                    initCalendar = viewModel.displayCalendarStart.value!!,
                                    type = type,
                                    category = category
                                )
                            )
                        }
                    }
                },
                GenericOnClickListener {
                    viewModel.swapType()
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.type.observe(viewLifecycleOwner) {
            (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.title =
                it
        }

        viewModel.detailTypeRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.detailTypeRV.adapter as DetailTypeRVAdapter).apply {
//                submitList(null)
                submitList2(it)
            }
        }
    }

}