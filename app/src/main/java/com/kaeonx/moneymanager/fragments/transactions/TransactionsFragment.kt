package com.kaeonx.moneymanager.fragments.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentTransactionsBinding
import com.kaeonx.moneymanager.userrepository.domain.Transaction

private const val TAG = "transactionFrag"

// TODO: navigate back to current day / month (when month selector is active)

//class TransactionsFragment : Fragment(), TransactionBottomSheetDialogFragment.TBSDFListener {
class TransactionsFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val viewModelFactory by lazy { TransactionsFragmentViewModelFactory(requireActivity().application, authViewModel.currentUser.value!!.uid) }
    private val viewModel: TransactionsFragmentViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentTransactionsBinding
//    private lateinit var transactionsRVAdapter: TransactionsRVAdapter
    private var isExpanded = false
    private var firstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        binding.transactionsRV.adapter = TransactionsRVAdapter(TransactionOnClickListener { transaction ->
            Toast.makeText(requireContext(), "Oh? You want ${transaction.txnId}?", Toast.LENGTH_SHORT).show()
        })
        viewModel.dayTransactions.observe(viewLifecycleOwner) {
            (binding.transactionsRV.adapter as TransactionsRVAdapter).submitList(it)
        }

//        binding.lifecycleOwner = this
//        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // After a configuration change or process death, the currentBackStackEntry
        // points to the dialog destination, so you must use getBackStackEntry()
        // with the specific ID of your destination to ensure we always
        // get the right NavBackStackEntry
        findNavController()
            .getBackStackEntry(R.id.transactionsFragment)
            .savedStateHandle
            .getLiveData<Transaction>("tbsdf_result")
            .observe(viewLifecycleOwner) {
                viewModel.addTxn(it)
            }
    }

    /*
    private fun setMonthYearPickerListeners() {
        tcttlJanBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlJanBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlFebBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlFebBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlMarBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlMarBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlAprBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlAprBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlMayBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlMayBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlJunBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlJunBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlJulBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlJulBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlAugBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlAugBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlSepBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlSepBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlOctBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlOctBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlNovBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlNovBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlDecBT.setOnClickListener {
            firebaseViewModel.setMonth(Integer.parseInt(tcttlDecBT.tag.toString()))
            transactionsABL.setExpanded(false, true)
        }
        tcttlLeftArrowBT.setOnClickListener { firebaseViewModel.yearMinusOne() }
        tcttlRightArrowBT.setOnClickListener { firebaseViewModel.yearPlusOne() }
        tcttlYearBT.setOnClickListener {
            YearPickerDialogFragment().show(childFragmentManager, "yearPicker")
        }
    }
     */

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        // Setup of Toolbar
//        requireActivity().mainActivityToolbar.inflateMenu(R.menu.main)
//        requireActivity().mainActivityToolbar.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.app_bar_sync -> {
//                    Toast.makeText(requireContext(), "Fragment!", Toast.LENGTH_LONG).show()
//                    true
//                }
//                else -> { false }
//            }
//        }

        // Setup of FAB
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityFAB.setOnClickListener {
            findNavController().navigate(TransactionsFragmentDirections.actionTransactionsFragmentToTransactionsBSDF(Transaction()))
        }
    }
}

////            // Courtesy of https://medium.com/androiddevelopers/appcompat-v23-2-daynight-d10f90c83e94
////            val newMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
////                Configuration.UI_MODE_NIGHT_NO -> {
////                    // Night mode is not active, we're in day time
////                    AppCompatDelegate.MODE_NIGHT_YES
////                }
////                Configuration.UI_MODE_NIGHT_YES -> {
////                    // Night mode is active, we're at night!
////                    AppCompatDelegate.MODE_NIGHT_NO
////                }
////                else -> {
////                    throw Exception("OI")
////                }
////            }
////            Toast.makeText(requireContext(), "Switching to mode $newMode", Toast.LENGTH_LONG).show()
////            AppCompatDelegate.setDefaultNightMode(newMode)