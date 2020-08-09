package com.kaeonx.moneymanager.fragments.budget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kaeonx.moneymanager.databinding.DialogItemQuickBudgetBinding

class QuickBudgetArrayAdapter(context: Context, list: List<QuickBudgetDialogItem>) :
    ArrayAdapter<QuickBudgetDialogItem>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            DialogItemQuickBudgetBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            // Courtesy of https://stackoverflow.com/a/58902295/7254995
            // Somehow there's this problem.
            convertView.tag as DialogItemQuickBudgetBinding
        }
        binding.quickBudgetDialogItem = getItem(position)!!
        binding.root.tag = binding
        return binding.root
    }
}

data class QuickBudgetDialogItem(
    val mainTitleText: String,
    val monthText: String,
    val currencyText: String,
    val amountText: String
)