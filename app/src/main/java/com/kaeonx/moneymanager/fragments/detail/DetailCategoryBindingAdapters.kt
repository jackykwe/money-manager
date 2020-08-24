package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.databinding.LlItemDetailTypeNoDataBinding
import com.kaeonx.moneymanager.databinding.RvLlItemDetailCategoryBinding
import com.kaeonx.moneymanager.fragments.transactions.TransactionOnClickListener

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_category_transactions
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter(
    "transactionsLL_transactionLLData",
    "transactionsLL_onClickListener",
    "transactionsLL_onLongClickListener"
)
fun LinearLayout.setTransactionsLLAdapter(
    list: List<DetailCategoryTransactionLLData>,
    itemOnClickListener: TransactionOnClickListener,
    itemOnLongClickListener: TransactionOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    if (list.isEmpty()) {
        addView(LlItemDetailTypeNoDataBinding.inflate(layoutInflater, null, false).root)
    } else {
        for (categoryTransactionLLData in list) {
            val itemBinding = RvLlItemDetailCategoryBinding.inflate(layoutInflater, null, false)
            itemBinding.categoryTransactionLLData = categoryTransactionLLData
            itemBinding.onClickListener = itemOnClickListener
            itemBinding.detailCategoryCL.setOnLongClickListener { view ->
                itemOnLongClickListener.onClick(
                    view,
                    categoryTransactionLLData.transaction.transactionId!!
                )
                true
            }
            itemBinding.executePendingBindings()
            addView(itemBinding.root)
        }
    }
}