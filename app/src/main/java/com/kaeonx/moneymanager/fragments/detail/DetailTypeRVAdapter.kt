package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.RvItemDetailTypeCategoriesBinding
import com.kaeonx.moneymanager.databinding.RvItemDetailTypeSummaryBinding
import com.kaeonx.moneymanager.userrepository.domain.IconDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUMMARY = 0
private const val CATEGORIES = 1

class DetailTypeRVAdapter(
    private val itemOnClickListener: DetailTypeOnClickListener,
    private val pieCentreClickListener: GenericOnClickListener
) :
    ListAdapter<DetailTypeRVItem, RecyclerView.ViewHolder>(DetailTypeRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(detailTypeRVPacket: DetailTypeRVPacket) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                DetailTypeRVItem.DetailTypeRVItemSummary(detailTypeRVPacket),
                DetailTypeRVItem.DetailTypeRVItemCategories(detailTypeRVPacket)
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
            is DetailTypeRVItem.DetailTypeRVItemSummary -> SUMMARY
            is DetailTypeRVItem.DetailTypeRVItemCategories -> CATEGORIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> DetailTypeSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            CATEGORIES -> DetailTypeCategoriesViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailTypeSummaryViewHolder -> {
                val data =
                    (getItem(position) as DetailTypeRVItem.DetailTypeRVItemSummary).detailTypeRVPacket
                holder.rebind(data, pieCentreClickListener)
            }
            is DetailTypeCategoriesViewHolder -> {
                val data =
                    (getItem(position) as DetailTypeRVItem.DetailTypeRVItemCategories).detailTypeRVPacket
                holder.rebind(data, itemOnClickListener)
            }
        }
    }

    class DetailTypeSummaryViewHolder private constructor(private val binding: RvItemDetailTypeSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: DetailTypeRVPacket, pieCentreClickListener: GenericOnClickListener) {
            binding.packet = newPacket
            binding.pieCentreClickListener = pieCentreClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): DetailTypeSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemDetailTypeSummaryBinding.inflate(layoutInflater, parent, false)
                return DetailTypeSummaryViewHolder(binding)
            }
        }
    }

    class DetailTypeCategoriesViewHolder private constructor(private val binding: RvItemDetailTypeCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: DetailTypeRVPacket, onClickListener: DetailTypeOnClickListener) {
            binding.packet = newPacket
            binding.onClickListener = onClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): DetailTypeCategoriesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemDetailTypeCategoriesBinding.inflate(layoutInflater, parent, false)
                return DetailTypeCategoriesViewHolder(binding)
            }
        }
    }
}

class DetailTypeRVItemDiffCallback : DiffUtil.ItemCallback<DetailTypeRVItem>() {
    override fun areItemsTheSame(
        oldItemDetailType: DetailTypeRVItem,
        newItemDetailType: DetailTypeRVItem
    ): Boolean {
        return oldItemDetailType.rvItemId == newItemDetailType.rvItemId
    }

    override fun areContentsTheSame(
        oldItemDetailType: DetailTypeRVItem,
        newItemDetailType: DetailTypeRVItem
    ): Boolean {
        return oldItemDetailType == newItemDetailType
    }
}

class DetailTypeOnClickListener(val clickListener: (type: String, category: String) -> Unit) {
    fun onClick(type: String, category: String) = clickListener(type, category)
}

// Can reuse
//class GenericOnClickListener(val clickListener: () -> Unit) {
//    fun onClick() = clickListener()
//}

sealed class DetailTypeRVItem {
    abstract val rvItemId: Int

    data class DetailTypeRVItemSummary(val detailTypeRVPacket: DetailTypeRVPacket) :
        DetailTypeRVItem() {
        override val rvItemId: Int = 0
    }

    data class DetailTypeRVItemCategories(val detailTypeRVPacket: DetailTypeRVPacket) :
        DetailTypeRVItem() {
        override val rvItemId: Int = 1
    }

}

data class DetailTypeRVPacket(
    val summaryType: String,
    val summaryPieData: PieData?,
    val summaryLegendLLData: List<DetailTypeLegendLLData>,
    val categoriesRangeString: String,
    val categoriesShowRangeCurrency: Boolean,
    val categoriesRangeCurrency: String,
    val categoriesRangeAmount: String,
    val categoryLLData: List<DetailTypeCategoryLLData>
)

data class DetailTypeLegendLLData(
    val colour: Int,
    val categoryName: String,
    val categoryPercent: String
)

data class DetailTypeCategoryLLData(
    val iconDetail: IconDetail,
    val type: String,
    val categoryName: String,
    val categoryPercent: String,
    val showCurrency: Boolean,
    val currency: String,
    val categoryAmount: String,
    val barData: BarData
)

