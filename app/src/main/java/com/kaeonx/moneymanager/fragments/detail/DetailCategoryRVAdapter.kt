package com.kaeonx.moneymanager.fragments.detail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.kaeonx.moneymanager.databinding.RvItemDetailCategorySummaryBinding
import com.kaeonx.moneymanager.databinding.RvItemDetailCategoryTransactionsBinding
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUMMARY = 0
private const val CATEGORIES = 1

class DetailCategoryRVAdapter(private val itemTypeOnClickListener: DetailCategoryOnClickListener) :
    ListAdapter<DetailCategoryRVItem, RecyclerView.ViewHolder>(DetailCategoryRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(detailCategoryRVPacket: DetailCategoryRVPacket) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                DetailCategoryRVItem.DetailCategoryRVItemSummary(detailCategoryRVPacket),
                DetailCategoryRVItem.DetailCategoryRVItemCategories(detailCategoryRVPacket)
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
            is DetailCategoryRVItem.DetailCategoryRVItemSummary -> SUMMARY
            is DetailCategoryRVItem.DetailCategoryRVItemCategories -> CATEGORIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> DetailCategorySummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            CATEGORIES -> DetailCategoryTransactionsViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailCategorySummaryViewHolder -> {
                val data =
                    (getItem(position) as DetailCategoryRVItem.DetailCategoryRVItemSummary).detailCategoryRVPacket
                holder.rebind(data)
            }
            is DetailCategoryTransactionsViewHolder -> {
                val data =
                    (getItem(position) as DetailCategoryRVItem.DetailCategoryRVItemCategories).detailCategoryRVPacket
                holder.rebind(data, itemTypeOnClickListener)
            }
        }
    }

    class DetailCategorySummaryViewHolder private constructor(private val binding: RvItemDetailCategorySummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: DetailCategoryRVPacket) {
            binding.packet = newPacket
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): DetailCategorySummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemDetailCategorySummaryBinding.inflate(layoutInflater, parent, false)
                return DetailCategorySummaryViewHolder(binding)
            }
        }
    }

    class DetailCategoryTransactionsViewHolder private constructor(private val binding: RvItemDetailCategoryTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            newPacket: DetailCategoryRVPacket,
            onClickListener: DetailCategoryOnClickListener
        ) {
            binding.packet = newPacket
            binding.onClickListener = onClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): DetailCategoryTransactionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemDetailCategoryTransactionsBinding.inflate(layoutInflater, parent, false)
                return DetailCategoryTransactionsViewHolder(binding)
            }
        }
    }
}

class DetailCategoryRVItemDiffCallback : DiffUtil.ItemCallback<DetailCategoryRVItem>() {
    override fun areItemsTheSame(
        oldItemDetailCategory: DetailCategoryRVItem,
        newItemDetailCategory: DetailCategoryRVItem
    ): Boolean {
        return oldItemDetailCategory.rvItemId == newItemDetailCategory.rvItemId
    }

    override fun areContentsTheSame(
        oldItemDetailCategory: DetailCategoryRVItem,
        newItemDetailCategory: DetailCategoryRVItem
    ): Boolean {
        return oldItemDetailCategory == newItemDetailCategory
    }
}

class DetailCategoryOnClickListener(val clickListener: (transactionId: Int) -> Unit) {
    fun onClick(transactionId: Int) = clickListener(transactionId)
}

sealed class DetailCategoryRVItem {
    abstract val rvItemId: Int

    data class DetailCategoryRVItemSummary(val detailCategoryRVPacket: DetailCategoryRVPacket) :
        DetailCategoryRVItem() {
        override val rvItemId: Int = 0
    }

    data class DetailCategoryRVItemCategories(val detailCategoryRVPacket: DetailCategoryRVPacket) :
        DetailCategoryRVItem() {
        override val rvItemId: Int = 1
    }
}

data class DetailCategoryRVPacket(
    val summaryCategory: String,
    val summaryLineData: LineData,
    val transactionsRangeString: String,
    val transactionsShowRangeCurrency: Boolean,
    val transactionsRangeCurrency: String,
    val transactionsRangeAmount: String,
    val transactionLLData: List<DetailCategoryTransactionLLData>
)

data class DetailCategoryTransactionLLData(
    val transaction: Transaction,
    val transactionPercent: String,
    val showCurrency: Boolean,
    val barData: BarData
)

