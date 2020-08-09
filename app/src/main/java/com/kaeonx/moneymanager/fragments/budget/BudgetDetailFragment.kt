package com.kaeonx.moneymanager.fragments.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kaeonx.moneymanager.databinding.FragmentBudgetEditBinding

class BudgetDetailFragment : Fragment() {

    private lateinit var binding: FragmentBudgetEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetEditBinding.inflate(inflater, container, false)
        return binding.root
    }

}