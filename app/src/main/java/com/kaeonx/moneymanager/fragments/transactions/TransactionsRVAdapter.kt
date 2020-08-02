package com.kaeonx.moneymanager.fragments.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.databinding.RvItemTransactionsDayBinding
import com.kaeonx.moneymanager.databinding.RvItemTransactionsHeaderBinding
import com.kaeonx.moneymanager.databinding.RvItemTransactionsSummaryBinding
import com.kaeonx.moneymanager.databinding.RvLlItemTransactionBinding
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val HEADER = 0
private const val SUMMARY = 1
private const val DAY_TRANSACTIONS = 2
private const val TAG = "trva"

class TransactionsRVAdapter(
    private val itemOnClickListener: TransactionOnClickListener,
    private val leftArrowClickListener: GenericOnClickListener,
    private val monthClickListener: GenericOnClickListener,
    private val rightArrowClickListener: GenericOnClickListener
) :
    ListAdapter<RVItem, RecyclerView.ViewHolder>(RVItemDiffCallback()) {

    fun submitListAndAddHeaders(monthText: String, list: List<DayTransactions>) {
        CoroutineScope(Dispatchers.Default).launch {
            val addable = listOf(RVItem.RVItemHeader(monthText), RVItem.RVItemSummary)
            val submittable = when {
                list.isEmpty() -> addable
                else -> addable + list.map { RVItem.RVItemDayTransactions(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(submittable)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RVItem.RVItemHeader -> HEADER
            is RVItem.RVItemSummary -> SUMMARY
            is RVItem.RVItemDayTransactions -> DAY_TRANSACTIONS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> TransactionsHeaderViewHolder.inflateAndCreateViewHolderFrom(parent)
            SUMMARY -> TransactionsSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            DAY_TRANSACTIONS -> TransactionsDayViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: ${viewType}. viewType must be either 0 or 1.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionsDayViewHolder -> {
                val item = (getItem(position) as RVItem.RVItemDayTransactions).dayTransactions
                holder.rebind(item, itemOnClickListener)
            }
            is TransactionsSummaryViewHolder -> Unit
            is TransactionsHeaderViewHolder -> {
                val monthText = (getItem(position) as RVItem.RVItemHeader).monthText
                holder.rebind(
                    monthText,
                    leftArrowClickListener,
                    monthClickListener,
                    rightArrowClickListener
                )
            }
        }
    }

    class TransactionsDayViewHolder private constructor(private val binding: RvItemTransactionsDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(item: DayTransactions, itemOnClickListener: TransactionOnClickListener) {
            binding.dayTransactions = item
//            binding.executePendingBindings()
            // It is always a good idea to execute pending bindings when using binding adapters
            // in a RecyclerView, since it can be slightly faster to size the views.

            binding.dayTransactionsLL.removeAllViews()
            val layoutInflater = LayoutInflater.from(binding.dayTransactionsLL.context)
            for (transaction in item.transactions) {
                val itemBinding = RvLlItemTransactionBinding.inflate(layoutInflater, null, false)
                itemBinding.transaction = transaction
                itemBinding.onClickListener = itemOnClickListener
//                itemBinding.executePendingBindings()
                binding.dayTransactionsLL.addView(itemBinding.root)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsDayViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
//                val view = layoutInflater.inflate(R.layout.rv_item_transactions_day, parent, false)
                val binding = RvItemTransactionsDayBinding.inflate(layoutInflater, parent, false)
                return TransactionsDayViewHolder(binding)
            }
        }
    }

    class TransactionsSummaryViewHolder private constructor(binding: RvItemTransactionsSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemTransactionsSummaryBinding.inflate(layoutInflater, parent, false)
                return TransactionsSummaryViewHolder(binding)
            }
        }
    }

    class TransactionsHeaderViewHolder private constructor(private val binding: RvItemTransactionsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            monthText: String,
            leftArrowClickListener: GenericOnClickListener,
            monthClickListener: GenericOnClickListener,
            rightArrowClickListener: GenericOnClickListener
        ) {
            binding.leftArrowClickListener = leftArrowClickListener
            binding.monthClickListener = monthClickListener
            binding.monthText = monthText
            binding.rightArrowClickListener = rightArrowClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemTransactionsHeaderBinding.inflate(layoutInflater, parent, false)
                return TransactionsHeaderViewHolder(binding)
            }
        }
    }
}

class RVItemDiffCallback : DiffUtil.ItemCallback<RVItem>() {
    override fun areItemsTheSame(oldItem: RVItem, newItem: RVItem): Boolean {
        return oldItem.rvItemId == newItem.rvItemId
    }

    override fun areContentsTheSame(oldItem: RVItem, newItem: RVItem): Boolean {
        return oldItem == newItem
    }
}

class TransactionOnClickListener(val clickListener: (transaction: Transaction) -> Unit) {
    fun onClick(transaction: Transaction) = clickListener(transaction)
}

class GenericOnClickListener(val clickListener: () -> Unit) {
    fun onClick() = clickListener()
}


sealed class RVItem {
    data class RVItemDayTransactions(val dayTransactions: DayTransactions) : RVItem() {
        override val rvItemId: Int = dayTransactions.dayOfMonth
    }

    data class RVItemHeader(val monthText: String) : RVItem() {
        override val rvItemId: Int = Int.MIN_VALUE
    }

    object RVItemSummary : RVItem() {
        override val rvItemId: Int = Int.MIN_VALUE + 1
    }

    abstract val rvItemId: Int
}