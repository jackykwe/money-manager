package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.kaeonx.moneymanager.chartcomponents.LineChartPacket
import com.kaeonx.moneymanager.chartcomponents.PieChartWLPacket
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.ChartComponentLineCardBinding
import com.kaeonx.moneymanager.databinding.ChartComponentPieCardWithLegendBinding
import com.kaeonx.moneymanager.databinding.RvItemDetailTypeCategoriesBinding
import com.kaeonx.moneymanager.userrepository.domain.IconDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUMMARY = 0
private const val PLOT = 1
private const val CATEGORIES = 2

class DetailTypeRVAdapter(
    private val itemOnClickListener: DetailTypeOnClickListener,
    private val pieCentreClickListener: GenericOnClickListener?
) :
    ListAdapter<DetailTypeRVItem, RecyclerView.ViewHolder>(DetailTypeRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(detailTypeRVPacket: DetailTypeRVPacket) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                DetailTypeRVItem.DetailTypeRVItemSummary(detailTypeRVPacket),
                DetailTypeRVItem.DetailTypeRVItemPlot(detailTypeRVPacket),
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
            is DetailTypeRVItem.DetailTypeRVItemPlot -> PLOT
            is DetailTypeRVItem.DetailTypeRVItemCategories -> CATEGORIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> DetailTypeSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            PLOT -> DetailTypePlotViewHolder.inflateAndCreateViewHolderFrom(parent)
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
            is DetailTypePlotViewHolder -> {
                val data =
                    (getItem(position) as DetailTypeRVItem.DetailTypeRVItemPlot).detailTypeRVPacket
                holder.rebind(data)
            }
            is DetailTypeCategoriesViewHolder -> {
                val data =
                    (getItem(position) as DetailTypeRVItem.DetailTypeRVItemCategories).detailTypeRVPacket
                holder.rebind(data, itemOnClickListener)
            }
        }
    }

    class DetailTypeSummaryViewHolder private constructor(private val binding: ChartComponentPieCardWithLegendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: DetailTypeRVPacket, pieCentreClickListener: GenericOnClickListener?) {
            binding.packet = newPacket.pieChartWLPacket
            binding.pieCentreClickListener = pieCentreClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): DetailTypeSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ChartComponentPieCardWithLegendBinding.inflate(layoutInflater, parent, false)
                return DetailTypeSummaryViewHolder(binding)
            }
        }
    }

    class DetailTypePlotViewHolder private constructor(private val binding: ChartComponentLineCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: DetailTypeRVPacket) {
            binding.packet = newPacket.lineChartPacket
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): DetailTypePlotViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ChartComponentLineCardBinding.inflate(layoutInflater, parent, false)
                return DetailTypePlotViewHolder(binding)
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

sealed class DetailTypeRVItem {
    abstract val rvItemId: Int

    data class DetailTypeRVItemSummary(val detailTypeRVPacket: DetailTypeRVPacket) :
        DetailTypeRVItem() {
        override val rvItemId: Int = 0
    }

    data class DetailTypeRVItemPlot(val detailTypeRVPacket: DetailTypeRVPacket) :
        DetailTypeRVItem() {
        override val rvItemId: Int = 1
    }

    data class DetailTypeRVItemCategories(val detailTypeRVPacket: DetailTypeRVPacket) :
        DetailTypeRVItem() {
        override val rvItemId: Int = 2
    }

}

data class DetailTypeRVPacket(
    val pieChartWLPacket: PieChartWLPacket,
    val lineChartPacket: LineChartPacket,
    val categoriesRangeString: String,
    val categoriesShowRangeCurrency: Boolean,
    val categoriesRangeCurrency: String,
    val categoriesRangeAmount: String,
    val categoryLLData: List<DetailTypeCategoryLLData>
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