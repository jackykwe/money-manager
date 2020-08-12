package com.kaeonx.moneymanager.fragments.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.userrepository.UserRepository

internal const val ACC_PICKER_EDITABLE = "editable"

// Essentially the same code as AccountsDF, except for the childFragment arguments.
class AccountsFragment : Fragment() {

    internal val accountOnClickListener by lazy {
        AccountOnClickListener { account ->
            val cond1 = UserRepository.getInstance().accounts.value!!.size > 1
            val cond2 = account.name != "Add..."
            findNavController().run {
                if (currentDestination?.id == R.id.accountsFragment) {
                    navigate(
                        AccountsFragmentDirections.actionAccountsFragmentToAccountEditFragment(
                            account,
                            cond1 && cond2
                        )
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_accounts, container, false)

        val childFragment = AccountsDisplayFragment()
        childFragment.arguments = Bundle().apply {
            putBoolean(ACC_PICKER_EDITABLE, true)
        }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.childFragmentContainer, childFragment)
            .commit()

        return view
    }
}
