package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.databinding.RvLlItemDetailCategoryBinding

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_category_transactions
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("transactionsLL_transactionLLData", "transactionsLL_onClickListener")
fun LinearLayout.setTransactionsLLAdapter(
    list: List<DetailCategoryTransactionLLData>,
    itemOnClickListener: DetailCategoryOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (categoryTransactionLLData in list) {
        val itemBinding = RvLlItemDetailCategoryBinding.inflate(layoutInflater, null, false)
        itemBinding.categoryTransactionLLData = categoryTransactionLLData
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}