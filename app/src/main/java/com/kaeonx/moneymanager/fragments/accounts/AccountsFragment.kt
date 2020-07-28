package com.kaeonx.moneymanager.fragments.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kaeonx.moneymanager.R

internal const val ACC_PICKER_EDITABLE = "editable"
internal const val ACC_PICKER_LISTENER = "listener"

// Essentially the same code as AccountsDF, except for the childFragment arguments.
class AccountsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_accounts, container, false)

        val childFragment = AccountsDisplayFragment()
        childFragment.arguments = Bundle().apply {
            putBoolean(ACC_PICKER_EDITABLE, true)
            putSerializable(ACC_PICKER_LISTENER, AccountOnClickListener { account ->
                Toast.makeText(requireContext(), "Oh? You want $account?", Toast.LENGTH_SHORT).show()
//                findNavController().navigate(
//                    CategoriesFragmentDirections.actionRootCategoriesFragmentToRootCategoryEditFragment(
//                        type,
//                        category
//                    )
//                )
            })
        }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.childFragmentContainer, childFragment)
            .commit()

        return view
    }
}
