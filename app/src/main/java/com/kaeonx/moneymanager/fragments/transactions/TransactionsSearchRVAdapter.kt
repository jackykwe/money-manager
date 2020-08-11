package com.kaeonx.moneymanager.fragments.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.databinding.RvItemTransactionsSearchDayBinding
import com.kaeonx.moneymanager.databinding.RvItemTransactionsSearchSummaryBinding
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import kotlinx.coroutines.*

private const val SUMMARY = 0
private const val DAY_TRANSACTIONS = 1
private const val TAG = "trva"

class TransactionsSearchRVAdapter(private val itemOnClickListener: TransactionOnClickListener) :
    ListAdapter<TransactionsSummaryRVItem, RecyclerView.ViewHolder>(
        TransactionsSummaryRVItemDiffCallback()
    ) {

    private var initRun = true

    fun submitList2(newPacket: TransactionsSearchRVPacket) {
        CoroutineScope(Dispatchers.Default).launch {
            val addable = listOf(
                TransactionsSummaryRVItem.TransactionsSummaryRVItemSummary(
                    newPacket.resultText,
                    newPacket.searchQuery
                )
            )
            val submittable = when {
                newPacket.dayTransactions.isEmpty() -> addable
                else -> addable + newPacket.dayTransactions.map {
                    TransactionsSummaryRVItem.TransactionsSummaryRVItemDayTransactions(it)
                }
            }
            if (initRun) {
                delay(300L)
                initRun = false
            }
            withContext(Dispatchers.Main) {
                submitList(submittable)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TransactionsSummaryRVItem.TransactionsSummaryRVItemSummary -> SUMMARY
            is TransactionsSummaryRVItem.TransactionsSummaryRVItemDayTransactions -> DAY_TRANSACTIONS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> TransactionsSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            DAY_TRANSACTIONS -> TransactionsDayViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionsSummaryViewHolder -> {
                val newSummaryData =
                    getItem(position) as TransactionsSummaryRVItem.TransactionsSummaryRVItemSummary
                holder.rebind(newSummaryData.resultText, newSummaryData.searchQuery)
            }
            is TransactionsDayViewHolder -> {
                val item =
                    getItem(position) as TransactionsSummaryRVItem.TransactionsSummaryRVItemDayTransactions
                holder.rebind(item.dayTransactions, itemOnClickListener)
            }
        }
    }

    class TransactionsSummaryViewHolder private constructor(private val binding: RvItemTransactionsSearchSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(resultText: String, searchQuery: String) {
            binding.resultText = resultText
            binding.searchQuery = searchQuery
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemTransactionsSearchSummaryBinding.inflate(layoutInflater, parent, false)
                return TransactionsSummaryViewHolder(binding)
            }
        }
    }

    class TransactionsDayViewHolder private constructor(private val binding: RvItemTransactionsSearchDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(item: DayTransactions, itemOnClickListener: TransactionOnClickListener) {
            binding.dayTransactions = item
            binding.onClickListener = itemOnClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsDayViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemTransactionsSearchDayBinding.inflate(layoutInflater, parent, false)
                return TransactionsDayViewHolder(binding)
            }
        }
    }
}

class TransactionsSummaryRVItemDiffCallback : DiffUtil.ItemCallback<TransactionsSummaryRVItem>() {
    override fun areItemsTheSame(
        oldItem: TransactionsSummaryRVItem,
        newItem: TransactionsSummaryRVItem
    ): Boolean {
        return oldItem.rvItemId == newItem.rvItemId
    }

    override fun areContentsTheSame(
        oldItem: TransactionsSummaryRVItem,
        newItem: TransactionsSummaryRVItem
    ): Boolean {
        return oldItem == newItem
    }
}

sealed class TransactionsSummaryRVItem {
    data class TransactionsSummaryRVItemDayTransactions(val dayTransactions: DayTransactions) :
        TransactionsSummaryRVItem() {
        override val rvItemId: Int = dayTransactions.dayOfMonth
    }

    data class TransactionsSummaryRVItemSummary(val resultText: String, val searchQuery: String) :
        TransactionsSummaryRVItem() {
        override val rvItemId: Int = Int.MIN_VALUE
    }

    abstract val rvItemId: Int
}

data class TransactionsSearchRVPacket(
    val resultText: String,
    val searchQuery: String,
    val dayTransactions: List<DayTransactions>
)