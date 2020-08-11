package com.kaeonx.moneymanager.fragments.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.chartcomponents.PieChartWLPacket
import com.kaeonx.moneymanager.databinding.ChartComponentPieCardWithLegendBinding
import com.kaeonx.moneymanager.databinding.RvItemBudgetDetailCardBinding
import com.kaeonx.moneymanager.userrepository.domain.Budget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUMMARY = 0
private const val CARD = 1

class BudgetDetailRVAdapter(
    private val expensesOnClickListener: BudgetDetailOnClickListener,
    private val editOnClickListener: BudgetDetailOnClickListener
) : ListAdapter<BudgetDetailRVItem, RecyclerView.ViewHolder>(BudgetDetailRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(budgetDetailRVPacket: BudgetDetailRVPacket) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                BudgetDetailRVItem.BudgetDetailRVItemSummary(budgetDetailRVPacket),
                BudgetDetailRVItem.BudgetDetailRVItemCard(budgetDetailRVPacket)
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
            is BudgetDetailRVItem.BudgetDetailRVItemSummary -> SUMMARY
            is BudgetDetailRVItem.BudgetDetailRVItemCard -> CARD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> BudgetDetailSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            CARD -> BudgetDetailCardViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BudgetDetailSummaryViewHolder -> {
                val data =
                    (getItem(position) as BudgetDetailRVItem.BudgetDetailRVItemSummary).budgetDetailRVPacket
                holder.rebind(data)
            }
            is BudgetDetailCardViewHolder -> {
                val data =
                    (getItem(position) as BudgetDetailRVItem.BudgetDetailRVItemCard).budgetDetailRVPacket
                holder.rebind(data, expensesOnClickListener, editOnClickListener)
            }
        }
    }

    class BudgetDetailSummaryViewHolder private constructor(private val binding: ChartComponentPieCardWithLegendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: BudgetDetailRVPacket) {
            binding.packet = newPacket.pieChartWLPacket
            binding.pieCentreClickListener = null
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): BudgetDetailSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ChartComponentPieCardWithLegendBinding.inflate(layoutInflater, parent, false)
                return BudgetDetailSummaryViewHolder(binding)
            }
        }
    }

    class BudgetDetailCardViewHolder private constructor(private val binding: RvItemBudgetDetailCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            newPacket: BudgetDetailRVPacket,
            expensesOnClickListener: BudgetDetailOnClickListener,
            editOnClickListener: BudgetDetailOnClickListener
        ) {
            binding.packet = newPacket
            binding.expensesOnClickListener = expensesOnClickListener
            binding.editOnClickListener = editOnClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): BudgetDetailCardViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemBudgetDetailCardBinding.inflate(layoutInflater, parent, false)
                return BudgetDetailCardViewHolder(binding)
            }
        }
    }
}

class BudgetDetailRVItemDiffCallback : DiffUtil.ItemCallback<BudgetDetailRVItem>() {
    override fun areItemsTheSame(
        oldItem: BudgetDetailRVItem,
        newItem: BudgetDetailRVItem
    ): Boolean {
        return oldItem.rvItemId == newItem.rvItemId
    }

    override fun areContentsTheSame(
        oldItem: BudgetDetailRVItem,
        newItem: BudgetDetailRVItem
    ): Boolean {
        return oldItem == newItem
    }
}

class BudgetDetailOnClickListener(val clickListener: (budget: Budget) -> Unit) {
    fun onClick(budget: Budget) = clickListener(budget)
}

sealed class BudgetDetailRVItem {
    abstract val rvItemId: Int

    data class BudgetDetailRVItemSummary(val budgetDetailRVPacket: BudgetDetailRVPacket) :
        BudgetDetailRVItem() {
        override val rvItemId: Int = 0
    }

    data class BudgetDetailRVItemCard(val budgetDetailRVPacket: BudgetDetailRVPacket) :
        BudgetDetailRVItem() {
        override val rvItemId: Int = 1
    }

}

data class BudgetDetailRVPacket(
    val pieChartWLPacket: PieChartWLPacket,
    val budgetText: String,
    val budgetLLData: BudgetLLData
)