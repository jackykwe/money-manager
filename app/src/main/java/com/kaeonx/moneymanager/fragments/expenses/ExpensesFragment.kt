package com.kaeonx.moneymanager.fragments.expenses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.databinding.FragmentExpensesBinding

private const val TAG = "exfrag"

class ExpensesFragment : Fragment() {

    private lateinit var binding: FragmentExpensesBinding

    private val args: ExpensesFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        ExpensesViewModelFactory(
            args.displayCalendar,
            args.showCurrency
        )
    }
    private val viewModel: ExpensesViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpensesBinding.inflate(inflater, container, false)
        binding.expensesRV.apply {
            setHasFixedSize(true)
            adapter = ExpensesRVAdapter(
                ExpensesOnClickListener {
                    Toast.makeText(requireContext(), "Oh? You want $it?", Toast.LENGTH_SHORT).show()
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.expensesRVPackage.observe(viewLifecycleOwner) {
            Log.d(TAG, "receied $it")
            (binding.expensesRV.adapter as ExpensesRVAdapter).apply {
                if (it == null) return@observe
                submitList(null)
                submitList2(it)
            }
        }
    }

}