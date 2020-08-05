package com.kaeonx.moneymanager.fragments.expenses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.databinding.RvItemExpensesCategoriesBinding
import com.kaeonx.moneymanager.databinding.RvItemExpensesSummaryBinding
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

    fun submitList2(expensesRVData: ExpensesRVData) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                ExpensesRVItem.ExpensesRVItemSummary(expensesRVData.pieData),
                ExpensesRVItem.ExpensesRVItemCategories(expensesRVData.expensesCategoriesData)
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
            CATEGORIES -> ExpensesCategoriesViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExpensesSummaryViewHolder -> {
                val newPieData = (getItem(position) as ExpensesRVItem.ExpensesRVItemSummary).pieData
                holder.rebind(newPieData)
            }
            is ExpensesCategoriesViewHolder -> {
                val data =
                    (getItem(position) as ExpensesRVItem.ExpensesRVItemCategories).expensesCategoriesData
                holder.rebind(data, itemOnClickListener)
            }
        }
    }

    class ExpensesSummaryViewHolder private constructor(private val binding: RvItemExpensesSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPieData: PieData?) {
            binding.pieData = newPieData
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

    class ExpensesCategoriesViewHolder private constructor(private val binding: RvItemExpensesCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newData: ExpensesCategoriesData, itemOnClickListener: ExpensesOnClickListener) {
            binding.data = newData
            binding.onClickListener = itemOnClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): ExpensesCategoriesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemExpensesCategoriesBinding.inflate(layoutInflater, parent, false)
                return ExpensesCategoriesViewHolder(binding)
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
    data class ExpensesRVItemSummary(val pieData: PieData?) : ExpensesRVItem() {
        override val rvItemId: Int = 0
    }

    data class ExpensesRVItemCategories(val expensesCategoriesData: ExpensesCategoriesData) :
        ExpensesRVItem() {
        override val rvItemId: Int = 1
    }

    abstract val rvItemId: Int
}

data class ExpensesCategoriesData(
    val monthString: String,
    val categories: List<ExpenseCategory>
)

data class ExpenseCategory(
    val categoryName: String,
    val showCurrency: Boolean,
    val currency: String,
    val categoryAmount: String,
    val barData: BarData?  // todo: change to non null
)

data class ExpensesRVData(
    val pieData: PieData?, // todo: change to non null
    val expensesCategoriesData: ExpensesCategoriesData
)