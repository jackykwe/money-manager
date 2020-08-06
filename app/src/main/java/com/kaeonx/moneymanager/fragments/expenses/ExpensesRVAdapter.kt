package com.kaeonx.moneymanager.fragments.expenses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.databinding.RvItemExpensesDetailBinding
import com.kaeonx.moneymanager.databinding.RvItemExpensesSummaryBinding
import com.kaeonx.moneymanager.userrepository.domain.IconDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUMMARY = 0
private const val CATEGORIES = 1
private const val TAG = "exrva"

class ExpensesRVAdapter(private val itemOnClickListener: ExpensesOnClickListener) :
    ListAdapter<ExpensesRVItem, RecyclerView.ViewHolder>(ExpensesRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(expensesRVPacket: ExpensesRVPacket) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                ExpensesRVItem.ExpensesRVItemSummary(expensesRVPacket),
                ExpensesRVItem.ExpensesRVItemCategories(expensesRVPacket)
            )
            if (initRun) {
                delay(300L)
                initRun = false
            }
            submitList(submittable)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ExpensesRVItem.ExpensesRVItemSummary -> SUMMARY
            is ExpensesRVItem.ExpensesRVItemCategories -> CATEGORIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> ExpensesSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            CATEGORIES -> ExpensesDetailViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExpensesSummaryViewHolder -> {
                val data =
                    (getItem(position) as ExpensesRVItem.ExpensesRVItemSummary).expensesRVPacket
                holder.rebind(data)
            }
            is ExpensesDetailViewHolder -> {
                val data =
                    (getItem(position) as ExpensesRVItem.ExpensesRVItemCategories).expensesRVPacket
                holder.rebind(data, itemOnClickListener)
            }
        }
    }

    class ExpensesSummaryViewHolder private constructor(private val binding: RvItemExpensesSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: ExpensesRVPacket) {
            binding.packet = newPacket
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): ExpensesSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemExpensesSummaryBinding.inflate(layoutInflater, parent, false)
                return ExpensesSummaryViewHolder(binding)
            }
        }
    }

    class ExpensesDetailViewHolder private constructor(private val binding: RvItemExpensesDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            newPacket: ExpensesRVPacket,
            itemOnClickListener: ExpensesOnClickListener
        ) {
            binding.packet = newPacket
            binding.onClickListener = itemOnClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): ExpensesDetailViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemExpensesDetailBinding.inflate(layoutInflater, parent, false)
                return ExpensesDetailViewHolder(binding)
            }
        }
    }
}

class ExpensesRVItemDiffCallback : DiffUtil.ItemCallback<ExpensesRVItem>() {
    override fun areItemsTheSame(
        oldItemExpenses: ExpensesRVItem,
        newItemExpenses: ExpensesRVItem
    ): Boolean {
        return oldItemExpenses.rvItemId == newItemExpenses.rvItemId
    }

    override fun areContentsTheSame(
        oldItemExpenses: ExpensesRVItem,
        newItemExpenses: ExpensesRVItem
    ): Boolean {
        return oldItemExpenses == newItemExpenses
    }
}

class ExpensesOnClickListener(val clickListener: (category: String) -> Unit) {
    fun onClick(category: String) = clickListener(category)
}

// Can reuse
//class GenericOnClickListener(val clickListener: () -> Unit) {
//    fun onClick() = clickListener()
//}

sealed class ExpensesRVItem {
    data class ExpensesRVItemSummary(val expensesRVPacket: ExpensesRVPacket) : ExpensesRVItem() {
        override val rvItemId: Int = 0
    }

    data class ExpensesRVItemCategories(val expensesRVPacket: ExpensesRVPacket) :
        ExpensesRVItem() {
        override val rvItemId: Int = 1
    }

    abstract val rvItemId: Int
}

data class ExpensesRVPacket(
    val summaryPieData: PieData?,
    val summaryLegendLLData: List<ExpensesLegendLLData>,
    val detailMonthString: String,
    val detailShowCurrency: Boolean,
    val detailCurrency: String,
    val detailMonthAmount: String,
    val detailLLData: List<ExpenseDetailLLData>
)

data class ExpensesLegendLLData(
    val colour: Int,
    val categoryName: String,
    val categoryPercent: String
)

data class ExpenseDetailLLData(
    val iconDetail: IconDetail,
    val categoryName: String,
    val categoryPercent: String,
    val showCurrency: Boolean,
    val currency: String,
    val categoryAmount: String,
    val barData: BarData
)

