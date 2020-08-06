package com.kaeonx.moneymanager.fragments.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.RvItemTransactionsDayBinding
import com.kaeonx.moneymanager.databinding.RvItemTransactionsHeaderBinding
import com.kaeonx.moneymanager.databinding.RvItemTransactionsSummaryBinding
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.*

private const val HEADER = 0
private const val SUMMARY = 1
private const val DAY_TRANSACTIONS = 2
private const val TAG = "trva"

class TransactionsRVAdapter(
    private val itemOnClickListener: TransactionOnClickListener,
    private val headerLeftArrowClickListener: GenericOnClickListener,
    private val headerMonthClickListener: GenericOnClickListener,
    private val headerRightArrowClickListener: GenericOnClickListener,
    private val summaryBudgetClickListener: GenericOnClickListener,
    private val summaryIncomeClickListener: GenericOnClickListener,
    private val summaryExpensesClickListener: GenericOnClickListener,
    private val summaryPieChartClickListener: GenericOnClickListener // show help fragment?
) :
    ListAdapter<TransactionsRVItem, RecyclerView.ViewHolder>(TransactionsRVItemDiffCallback()) {

    private var initRun = true

    fun submitListAndAddHeaders(
        newList: List<DayTransactions>,
        newHeaderData: String,
        newTransactionsSummaryData: TransactionsSummaryData
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            val addable = listOf(
                TransactionsRVItem.TransactionsRVItemHeader(newHeaderData),
                TransactionsRVItem.TransactionsRVItemSummary(newTransactionsSummaryData)
            )
            val submittable = when {
                newList.isEmpty() -> addable
                else -> addable + newList.map {
                    TransactionsRVItem.TransactionsRVItemDayTransactions(
                        it
                    )
                }
            }
            // For a smooth experience (for expanding AppBar on first launch / hamburger animation on navigateUp)
            // since submitList blocks the UI thread when updating the UI (cannot be avoided)
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
            is TransactionsRVItem.TransactionsRVItemHeader -> HEADER
            is TransactionsRVItem.TransactionsRVItemSummary -> SUMMARY
            is TransactionsRVItem.TransactionsRVItemDayTransactions -> DAY_TRANSACTIONS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> TransactionsHeaderViewHolder.inflateAndCreateViewHolderFrom(parent)
            SUMMARY -> TransactionsSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            DAY_TRANSACTIONS -> TransactionsDayViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionsHeaderViewHolder -> {
                val monthText =
                    (getItem(position) as TransactionsRVItem.TransactionsRVItemHeader).monthText
                holder.rebind(
                    monthText,
                    headerLeftArrowClickListener,
                    headerMonthClickListener,
                    headerRightArrowClickListener
                )
            }
            is TransactionsSummaryViewHolder -> {
                val newSummaryData =
                    (getItem(position) as TransactionsRVItem.TransactionsRVItemSummary).transactionsSummaryData
                holder.rebind(
                    newSummaryData,
                    summaryBudgetClickListener,
                    summaryIncomeClickListener,
                    summaryExpensesClickListener,
                    summaryPieChartClickListener
                )
            }
            is TransactionsDayViewHolder -> {
                val item =
                    (getItem(position) as TransactionsRVItem.TransactionsRVItemDayTransactions).dayTransactions
                holder.rebind(item, itemOnClickListener)
            }
        }
    }

    class TransactionsHeaderViewHolder private constructor(private val binding: RvItemTransactionsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            newMonthText: String,
            newLeftArrowClickListener: GenericOnClickListener,
            newMonthClickListener: GenericOnClickListener,
            newRightArrowClickListener: GenericOnClickListener
        ) {
            binding.apply {
                leftArrowClickListener = newLeftArrowClickListener
                monthClickListener = newMonthClickListener
                monthText = newMonthText
                rightArrowClickListener = newRightArrowClickListener
                executePendingBindings()
            }
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

    class TransactionsSummaryViewHolder private constructor(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            newTransactionsSummaryData: TransactionsSummaryData,
            summaryBudgetClickListener: GenericOnClickListener,
            summaryIncomeClickListener: GenericOnClickListener,
            summaryExpensesClickListener: GenericOnClickListener,
            summaryPieChartClickListener: GenericOnClickListener
        ) {
            when (binding) {
                is RvItemTransactionsSummaryBinding -> {
                    binding.apply {
                        summaryData = newTransactionsSummaryData
                        budgetListener = summaryBudgetClickListener
                        expensesListener = summaryExpensesClickListener
                        pieChartListener = summaryPieChartClickListener
                        executePendingBindings()
                    }
                }
                else -> throw TODO("Not supported yet")
            }
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemTransactionsSummaryBinding.inflate(layoutInflater, parent, false)
                return TransactionsSummaryViewHolder(binding)
            }
        }
    }

    class TransactionsDayViewHolder private constructor(private val binding: RvItemTransactionsDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(item: DayTransactions, itemOnClickListener: TransactionOnClickListener) {
            binding.apply {
                dayTransactions = item
                onClickListener = itemOnClickListener
                executePendingBindings()
            }
            // It is always a good idea to execute pending bindings when using binding adapters
            // in a RecyclerView, since it can be slightly faster to size the views.
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsDayViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemTransactionsDayBinding.inflate(layoutInflater, parent, false)
                return TransactionsDayViewHolder(binding)
            }
        }
    }
}

class TransactionsRVItemDiffCallback : DiffUtil.ItemCallback<TransactionsRVItem>() {
    override fun areItemsTheSame(
        oldItemTransactions: TransactionsRVItem,
        newItemTransactions: TransactionsRVItem
    ): Boolean {
        return oldItemTransactions.rvItemId == newItemTransactions.rvItemId
    }

    override fun areContentsTheSame(
        oldItemTransactions: TransactionsRVItem,
        newItemTransactions: TransactionsRVItem
    ): Boolean {
        return oldItemTransactions == newItemTransactions
    }
}

class TransactionOnClickListener(val clickListener: (transaction: Transaction) -> Unit) {
    fun onClick(transaction: Transaction) = clickListener(transaction)
}

sealed class TransactionsRVItem {
    data class TransactionsRVItemDayTransactions(val dayTransactions: DayTransactions) :
        TransactionsRVItem() {
        override val rvItemId: Int = dayTransactions.dayOfMonth
    }

    data class TransactionsRVItemHeader(val monthText: String) : TransactionsRVItem() {
        override val rvItemId: Int = Int.MIN_VALUE
    }

    data class TransactionsRVItemSummary(val transactionsSummaryData: TransactionsSummaryData) :
        TransactionsRVItem() {
        override val rvItemId: Int = Int.MIN_VALUE + 1
    }

    abstract val rvItemId: Int
}

data class TransactionsSummaryData(
    val homeCurrency: String,
    val showCurrencyMode1: Boolean,  // affects budget, expenses
    val showCurrencyMode2: Boolean,  // affects income, expenses, balance
    val budget: String,
    val monthIncome: String,
    val monthExpenses: String,
    val monthBalance: String,
    val pieData: PieData
)