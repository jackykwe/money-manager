package com.kaeonx.moneymanager.fragments.debt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity

// TODO Future
class DebtFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()
        return inflater.inflate(R.layout.fragment_debt, container, false)
    }

}
