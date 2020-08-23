package com.kaeonx.moneymanager.fragments.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.kaeonx.moneymanager.databinding.RvItemBudgetsBinding
import com.kaeonx.moneymanager.userrepository.domain.Budget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CATEGORIES = 0

class BudgetsRVAdapter(
    private val itemOnClickListener: BudgetOnClickListener,
    private val itemOnLongClickListener: BudgetOnClickListener
) :
    ListAdapter<BudgetRVItem, RecyclerView.ViewHolder>(BudgetRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(budgetsRVPacket: BudgetsRVPacket) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                BudgetRVItem.BudgetRVItemCategories(budgetsRVPacket)
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
            is BudgetRVItem.BudgetRVItemCategories -> CATEGORIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CATEGORIES -> BudgetCategoriesViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BudgetCategoriesViewHolder -> {
                val data =
                    (getItem(position) as BudgetRVItem.BudgetRVItemCategories).budgetsRVPacket
                holder.rebind(data, itemOnClickListener, itemOnLongClickListener)
            }
        }
    }

    class BudgetCategoriesViewHolder private constructor(private val binding: RvItemBudgetsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            newPacket: BudgetsRVPacket,
            onClickListener: BudgetOnClickListener,
            onLongClickListener: BudgetOnClickListener
        ) {
            binding.packet = newPacket
            binding.onClickListener = onClickListener
            binding.onLongClickListener = onLongClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): BudgetCategoriesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemBudgetsBinding.inflate(layoutInflater, parent, false)
                return BudgetCategoriesViewHolder(binding)
            }
        }
    }
}

class BudgetRVItemDiffCallback : DiffUtil.ItemCallback<BudgetRVItem>() {
    override fun areItemsTheSame(oldItem: BudgetRVItem, newItem: BudgetRVItem): Boolean {
        return oldItem.rvItemId == newItem.rvItemId
    }

    override fun areContentsTheSame(oldItem: BudgetRVItem, newItem: BudgetRVItem): Boolean {
        return oldItem == newItem
    }
}

sealed class BudgetRVItem {
    abstract val rvItemId: Int

    data class BudgetRVItemCategories(val budgetsRVPacket: BudgetsRVPacket) : BudgetRVItem() {
        override val rvItemId: Int = 0
    }
}

class BudgetOnClickListener(val clickListener: (view: View, category: String) -> Unit) {
    fun onClick(view: View, category: String) = clickListener(view, category)
}

data class BudgetsRVPacket(
    val budgetText: String,
    val budgetLLData: List<BudgetLLData>
)

data class BudgetLLData(
    val budget: Budget,
    val showCurrency: Boolean,
    val spentAmount: String,
    val spentPercent: String,
    val barData: BarData
)