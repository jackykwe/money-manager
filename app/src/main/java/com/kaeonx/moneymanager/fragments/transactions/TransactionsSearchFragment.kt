package com.kaeonx.moneymanager.fragments.transactions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentTransactionsSearchBinding

private const val TAG = "tsfrag"

class TransactionsSearchFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsSearchBinding

    private val args: TransactionsSearchFragmentArgs by navArgs()
    private val viewModelFactory by lazy { TransactionsSearchViewModelFactory(args.initSearchQuery) }
    private val viewModel: TransactionsSearchViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            if (menu.getItem(0).actionView !is SearchView) {
                menu.clear()
                inflateMenu(R.menu.fragment_transactions)
            }
            (menu.getItem(0).actionView as SearchView).apply {
                isIconified = false
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        // Close the keyboard, if it's open
                        val imm =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                        (menu.getItem(0).actionView as SearchView).clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        if (p0 != null) viewModel.reSearch(p0.trim())
                        return true
                    }
                })
                setQuery(viewModel.currentQuery, false)
                queryHint = "Find in memo"
            }
        }
        binding = FragmentTransactionsSearchBinding.inflate(inflater, container, false)
        binding.transactionsRV.apply {
            setHasFixedSize(true)
            adapter = TransactionsSearchRVAdapter(
                itemOnClickListener = TransactionOnClickListener { transaction ->
                    // Close the keyboard, if it's open
                    val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                    findNavController().run {
                        if (currentDestination?.id == R.id.transactionsSearchFragment) {
                            navigate(
                                TransactionsSearchFragmentDirections.actionTransactionsSearchFragmentToTransactionEditFragment(
                                    transaction.transactionId!!
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
        viewModel.transactionsSearchRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.transactionsRV.adapter as TransactionsSearchRVAdapter).apply {
                submitList2(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // UP button behaviour
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.setNavigationOnClickListener {
            // Close the keyboard, if it's open
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            findNavController().navigateUp()
        }
    }
}