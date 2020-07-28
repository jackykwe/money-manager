package com.kaeonx.moneymanager.fragments.accounts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.kaeonx.moneymanager.R

internal const val ACCOUNTS_DF_RESULT = "accounts_df_result"

// Essentially the same code as AccountsFragment, except for the childFragment arguments.
class AccountsDF: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_accounts, container, false)

        val childFragment = AccountsDisplayFragment()
        childFragment.arguments = Bundle().apply {
            putBoolean(ACC_PICKER_EDITABLE, false)
            putSerializable(ACC_PICKER_LISTENER, AccountOnClickListener { account ->
                findNavController().getBackStackEntry(R.id.transactionsBSDF).savedStateHandle.set(ACCOUNTS_DF_RESULT, account)
                findNavController().navigateUp()
            })
        }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.childFragmentContainer, childFragment)
            .commit()

        return view
    }
}
