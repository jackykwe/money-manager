package com.kaeonx.moneymanager.fragments.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.customclasses.toFormattedString
import com.kaeonx.moneymanager.customclasses.toIconHex
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import kotlinx.android.synthetic.main.icon_transaction.view.*
import kotlinx.android.synthetic.main.rv_ll_item_transaction.view.*
import java.util.*

private const val SUMMARY = 0
private const val DAY_TRANSACTIONS = 1
private const val TAG = "trva"

class TransactionsRVAdapter : ListAdapter<DayTransactions, RecyclerView.ViewHolder>(DayTransactionsDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> SUMMARY
            else -> DAY_TRANSACTIONS
        }
    }

    override fun getItemCount() = currentList.size + 1
//    override fun getItemCount() = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> TransactionsSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            DAY_TRANSACTIONS -> TransactionsDayViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: ${viewType}. viewType must be either 0 or 1.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionsDayViewHolder -> holder.rebind(getItem(position - 1))
            is TransactionsSummaryViewHolder -> { }
        }
    }

    class TransactionsDayViewHolder private constructor(private val containerView: View) : RecyclerView.ViewHolder(containerView) {
        var dayDateTV: TextView = containerView.findViewById(R.id.dayDateTV)
        var dayIncomeHintTV: TextView = containerView.findViewById(R.id.dayIncomeHintTV)
        var dayIncomeCurrencyTV: TextView = containerView.findViewById(R.id.dayIncomeCurrencyTV)
        var dayIncomeAmountTV: TextView = containerView.findViewById(R.id.dayIncomeAmountTV)
        var dayExpensesHintTV: TextView = containerView.findViewById(R.id.dayExpensesHintTV)
        var dayExpensesCurrencyTV: TextView = containerView.findViewById(R.id.dayExpensesCurrencyTV)
        var dayExpensesAmountTV: TextView = containerView.findViewById(R.id.dayExpensesAmountTV)
        var dayTransactionsLL: LinearLayout = containerView.findViewById(R.id.dayTransactionsLL)

        fun rebind(item: DayTransactions) {
            // due to the check in getItemCount, once the code reaches here,
            // monthTransactions must be non-null.
            dayDateTV.text = item.ymdCalendar.toFormattedString("EEE ddMMyy")

            if (item.dayIncome != null) {
                dayIncomeHintTV.visibility = View.VISIBLE
                dayIncomeCurrencyTV.apply {
                    visibility = if (item.incomeAllHome && false) {  //false for hide if home currency match
                        View.GONE
                    } else {
                        text = "SGD" // todo: home currency
                        View.VISIBLE
                    }
                }
                dayIncomeAmountTV.apply {
                    visibility = View.VISIBLE
                    text = item.dayIncome
                }
            } else {  // Necessary, because the ViewHolders are reused!!!
                dayIncomeHintTV.visibility = View.GONE
                dayIncomeCurrencyTV.visibility = View.GONE
                dayIncomeAmountTV.visibility = View.GONE
            }

            if (item.dayExpenses != null) {
                dayExpensesHintTV.visibility = View.VISIBLE
                dayExpensesCurrencyTV.apply {
                    visibility = if(item.expensesAllHome && false) {  //false for hide if home currency match
                        View.GONE
                    } else {
                        text = "SGD" // todo: home currency
                        View.VISIBLE
                    }
                }
                dayExpensesAmountTV.apply {
                    visibility = View.VISIBLE
                    text = item.dayExpenses
                }
            } else {  // Necessary, because the ViewHolders are reused!!!
                dayExpensesHintTV.visibility = View.GONE
                dayExpensesCurrencyTV.visibility = View.GONE
                dayExpensesAmountTV.visibility = View.GONE
            }

            dayTransactionsLL.removeAllViews()
            for (transaction in item.transactions) {
                val childView = LayoutInflater.from(containerView.context).inflate(R.layout.rv_ll_item_transaction, null)
                dayTransactionsLL.addView(childView)
                childView.transactionMemoTV.text = "[${transaction.txnId}] ${transaction.memo}"

                if (transaction.originalCurrency == "SGD" && false) {
                    childView.transactionCurrencyTV.visibility = View.GONE
                } else {
                    childView.transactionCurrencyTV.visibility = View.VISIBLE
                    childView.transactionCurrencyTV.text = transaction.originalCurrency
                }
                childView.transactionValueTV.text = transaction.originalAmount

//                val category = CategoryIconHandler.getCategory(context, firebaseViewModel.currentUserLD.value!!.uid, transaction.type, transaction.category)
//                childView.iconBG.drawable.setTint(ColourHandler.getColourObject(context.resources, category.colourString))
                childView.iconTV.text = "F0011".toIconHex() //CategoryIconHandler.hexToIcon(category.iconHex)

//                val account = AccountHandler.getAccount(context, firebaseViewModel.currentUserLD.value!!.uid, transaction.account)
//                childView.iconRing.drawable.setTint(ColourHandler.getColourObject(context.resources, account.colourString))
//                childView.setOnClickListener {
//                    fragment.findNavController().navigate(RootTransactionsFragmentDirections.actionRootTransactionsFragmentToRootTransactionEditFragment(transaction))
//                }
            }
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsDayViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_transactions_day, parent, false)
                return TransactionsDayViewHolder(view)
            }
        }
    }

    class TransactionsSummaryViewHolder private constructor(containerView: View) : RecyclerView.ViewHolder(containerView) {
//    var summaryMonthTV: TextView = containerView.findViewById(R.id.summaryMonthTV)
//    var summaryLeftArrowTV: TextView = containerView.findViewById(R.id.summaryLeftArrowTV)
//    var summaryRightArrowTV: TextView = containerView.findViewById(R.id.summaryRightArrowTV)

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsSummaryViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_transactions_summary, parent, false)
                return TransactionsSummaryViewHolder(view)
            }
        }
    }
}

class DayTransactionsDiffCallback : DiffUtil.ItemCallback<DayTransactions>() {
    override fun areItemsTheSame(oldItem: DayTransactions, newItem: DayTransactions): Boolean {
        return oldItem.ymdCalendar.get(Calendar.DAY_OF_MONTH) == newItem.ymdCalendar.get(Calendar.DAY_OF_MONTH)
    }

    override fun areContentsTheSame(oldItem: DayTransactions, newItem: DayTransactions): Boolean {
        return oldItem == newItem
    }
}