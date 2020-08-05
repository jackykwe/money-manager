package com.kaeonx.moneymanager.fragments.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.kaeonx.moneymanager.databinding.FragmentAccountsDisplayBinding

class AccountsDisplayFragment: Fragment() {

    private lateinit var binding: FragmentAccountsDisplayBinding
    private val viewModel: AccountsDisplayViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountsDisplayBinding.inflate(inflater, container, false)

        binding.root.adapter = AccountsDisplayRVAdapter(
            requireArguments().getBoolean(ACC_PICKER_EDITABLE),
            requireArguments().getSerializable(ACC_PICKER_LISTENER) as AccountOnClickListener
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.accounts.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.root.adapter as AccountsDisplayRVAdapter).submitListAndAddTailIfNecessary(it)
        }
    }
}