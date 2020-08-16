package com.kaeonx.moneymanager.fragments.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentTransactionEditBinding

class TransactionEditFragment : Fragment() {
    // TODO: NOT OPTIMISED YET FOR SMOOTHNESS - INTRODUCE SOME DELAYS?

    private lateinit var binding: FragmentTransactionEditBinding

    private val args: TransactionEditFragmentArgs by navArgs()
    private val viewModelFactory by lazy { TransactionEditViewModelFactory(args.transactionId) }
    private val viewModel: TransactionEditViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            menu.clear()
            inflateMenu(R.menu.fragment_general_edit_deleteable)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.app_bar_delete -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Delete this transaction?")
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteTransaction()
                            }
                            .setNegativeButton(R.string.cancel) { _, _ -> }
                            .create()
                            .show()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }

        binding = FragmentTransactionEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.editTransactionFAB.setOnClickListener {
            findNavController().run {
                if (currentDestination?.id == R.id.transactionEditFragment) {
                    navigate(
                        TransactionEditFragmentDirections.actionTransactionEditFragmentToTransactionsBSDF(
                            viewModel.transaction.value!!
                        )
                    )
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.initShowContent.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.initShowContentHandled()
                binding.mainSV.visibility = View.VISIBLE
            }
        }

        viewModel.navigateUp.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.navigateUpHandled()
                findNavController().navigateUp()
            }
        }
    }

}