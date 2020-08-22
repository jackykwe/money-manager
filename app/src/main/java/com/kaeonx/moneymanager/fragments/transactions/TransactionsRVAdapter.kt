package com.kaeonx.moneymanager.fragments.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.*
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.*

private const val HEADER = 0
private const val SUMMARY_BUDGET = 1
private const val SUMMARY_NO_BUDGET = 2
private const val DAY_TRANSACTIONS = 3
private const val DAY_TRANSACTIONS_NO_DATA = 4

class TransactionsRVAdapter(
    private val itemOnClickListener: TransactionOnClickListener,
    private val itemOnLongClickListener: TransactionOnClickListener,
    private val headerLeftArrowClickListener: GenericOnClickListener,
    private val headerMonthClickListener: GenericOnClickListener,
    private val headerRightArrowClickListener: GenericOnClickListener,
    private val summaryBudgetClickListener: GenericOnClickListener,
    private val summaryIncomeClickListener: GenericOnClickListener,
    private val summaryExpensesClickListener: GenericOnClickListener,
    private val summaryPieChartClickListener: GenericOnClickListener
) :
    ListAdapter<TransactionsRVItem, RecyclerView.ViewHolder>(TransactionsRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(newPacket: TransactionsRVPacket) {
        CoroutineScope(Dispatchers.Default).launch {
            val addable = listOf(
                TransactionsRVItem.TransactionsRVItemHeader(newPacket.headerString),
                TransactionsRVItem.TransactionsRVItemSummary(newPacket.summaryData)
            )
            val noDataAppendable by lazy { listOf(TransactionsRVItem.TransactionsRVItemDayTransactionsNoData()) }
            val submittable = when {
                newPacket.newList.isEmpty() -> addable + noDataAppendable
                else -> addable + newPacket.newList.map {
                    TransactionsRVItem.TransactionsRVItemDayTransactions(it)
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
        return when (val item = getItem(position)) {
            is TransactionsRVItem.TransactionsRVItemHeader -> HEADER
            is TransactionsRVItem.TransactionsRVItemSummary -> {
                if (item.transactionsSummaryData.budget == null) SUMMARY_NO_BUDGET else SUMMARY_BUDGET
            }
            is TransactionsRVItem.TransactionsRVItemDayTransactions -> DAY_TRANSACTIONS
            is TransactionsRVItem.TransactionsRVItemDayTransactionsNoData -> DAY_TRANSACTIONS_NO_DATA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> TransactionsHeaderViewHolder.inflateAndCreateViewHolderFrom(parent)
            SUMMARY_BUDGET -> TransactionsSummaryViewHolder.inflateAndCreateViewHolderFrom(
                parent,
                SUMMARY_BUDGET
            )
            SUMMARY_NO_BUDGET -> TransactionsSummaryViewHolder.inflateAndCreateViewHolderFrom(
                parent,
                SUMMARY_NO_BUDGET
            )
            DAY_TRANSACTIONS -> TransactionsDayViewHolder.inflateAndCreateViewHolderFrom(parent)
            DAY_TRANSACTIONS_NO_DATA -> TransactionsDayNoDataViewHolder.inflateAndCreateViewHolderFrom(
                parent
            )
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
                holder.rebind(item, itemOnClickListener, itemOnLongClickListener)
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
                is RvItemTransactionsSummaryWithoutBudgetBinding -> {
                    binding.apply {
                        summaryData = newTransactionsSummaryData
                        incomeListener = summaryIncomeClickListener
                        expensesListener = summaryExpensesClickListener
                        executePendingBindings()
                    }
                }
                else -> throw IllegalArgumentException("Unknown binding passed: ${binding.javaClass}. How did you get here?")
            }
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(
                parent: ViewGroup,
                switch: Int
            ): TransactionsSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = when (switch) {
                    SUMMARY_BUDGET -> RvItemTransactionsSummaryBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                    SUMMARY_NO_BUDGET -> RvItemTransactionsSummaryWithoutBudgetBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                    else -> throw IllegalArgumentException("Unknown switch passed: $switch. Switch should be SUMMARY_BUDGET or SUMMARY_NO_BUDGET")
                }
                return TransactionsSummaryViewHolder(binding)
            }
        }
    }

    class TransactionsDayViewHolder private constructor(private val binding: RvItemTransactionsDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            item: DayTransactions,
            itemOnClickListener: TransactionOnClickListener,
            itemOnLongClickListener: TransactionOnClickListener
        ) {
            binding.apply {
                dayTransactions = item
                onClickListener = itemOnClickListener
                onLongClickListener = itemOnLongClickListener
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

    class TransactionsDayNoDataViewHolder private constructor(binding: RvItemNoDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsDayNoDataViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemNoDataBinding.inflate(layoutInflater, parent, false)
                return TransactionsDayNoDataViewHolder(binding)
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

class TransactionOnClickListener(val clickListener: (view: View, transaction: Transaction) -> Unit) {
    fun onClick(view: View, transaction: Transaction) = clickListener(view, transaction)
}

sealed class TransactionsRVItem {
    class TransactionsRVItemDayTransactionsNoData : TransactionsRVItem() {
        override val rvItemId: Int = Int.MIN_VALUE + 2
    }

    data class TransactionsRVItemDayTransactions(val dayTransactions: DayTransactions) :
        TransactionsRVItem() {
        override val rvItemId: Int = dayTransactions.ymdIdentifier
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

/**
 * @param showHomeCurrencyMode1 affects expenses
 * @param showHomeCurrencyMode2 affects income, expenses, balance
 */
data class TransactionsSummaryData(
    val showBudgetCurrency: Boolean?,
    val showHomeCurrencyMode1: Boolean?,
    val showHomeCurrencyMode2: Boolean,
    val budgetCurrency: String?,
    val budget: String?,
    val homeCurrency: String,
    val monthExpenses: String,
    val pieData: PieData?,
    val monthIncome: String,
    val monthBalance: String
)

data class TransactionsRVPacket(
    val newList: List<DayTransactions>,
    val headerString: String,
    val summaryData: TransactionsSummaryData
)