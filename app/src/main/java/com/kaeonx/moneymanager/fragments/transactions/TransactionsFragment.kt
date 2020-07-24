package com.kaeonx.moneymanager.fragments.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kaeonx.moneymanager.R

private const val TAG = "transactionFrag"

// TODO: navigate back to current day / month (when month selector is active)

//class TransactionsFragment : Fragment(), TransactionBottomSheetDialogFragment.TBSDFListener {
class TransactionsFragment : Fragment() {

//    private val viewModel: TransactionsFragmentViewModel by viewModels()
//    private lateinit var transactionsRVAdapter: TransactionsRVAdapter
    private var isExpanded = false
    private var firstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

//    private fun readDataFromJSON(uid: String, calendar: Calendar): ArrayList<DayTransactions>? {
//        Log.d(TAG, "readDataFromJSON called with month ${calendar.get(Calendar.MONTH)}")
//        return JSONHandler.readDayTransactions(uid, calendar)
//    }

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

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
////        (requireActivity() as AppCompatActivity).setSupportActionBar(rootTransactionsFragmentAppBar)
//
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
//
//        // Setup of FAB
//        requireActivity().mainActivityFAB.setOnClickListener {
//            TransactionBottomSheetDialogFragment(
//                this as TransactionBottomSheetDialogFragment.TBSDFListener,
//                null
//            ).show(childFragmentManager, null)
//
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
//        }
//
//        transactionsRV.layoutManager = LinearLayoutManager(requireActivity())
//
//        // When month has been changed
//        viewModel.transactionDisplayMonthLD.observe(viewLifecycleOwner, Observer {
//            transactionsRVAdapter = TransactionsRVAdapter(requireContext(), requireActivity(), this, readDataFromJSON((requireActivity() as MainActivity).loadedUserId!!, it))
//            transactionsRV.adapter = transactionsRVAdapter
//            transactionsRVAdapter.notifyDataSetChanged()
//        })
//    }

//    override fun onTBSDFResult(successful: Boolean, newTransaction: Transaction) {
//        if (successful) {
//            transactionsRVAdapter.loadNewData(readDataFromJSON((requireActivity() as MainActivity).loadedUserId!!, viewModel.transactionDisplayMonthLD.value!!))
//        } else {
//            Toast.makeText(requireContext(), "Save FAILED", Toast.LENGTH_SHORT).show()
//        }
//    }
}
