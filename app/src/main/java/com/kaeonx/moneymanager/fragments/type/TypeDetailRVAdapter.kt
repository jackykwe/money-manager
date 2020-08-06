package com.kaeonx.moneymanager.fragments.type

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.RvItemTypeDetailCategoriesBinding
import com.kaeonx.moneymanager.databinding.RvItemTypeDetailSummaryBinding
import com.kaeonx.moneymanager.userrepository.domain.IconDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUMMARY = 0
private const val CATEGORIES = 1
private const val TAG = "exrva"

class TypeDetailRVAdapter(
    private val itemOnClickListener: TypeDetailOnClickListener,
    private val pieCentreClickListener: GenericOnClickListener
) :
    ListAdapter<TypeDetailRVItem, RecyclerView.ViewHolder>(TypeDetailRVItemDiffCallback()) {

    private var initRun = true

    fun submitList2(typeRVPacket: TypeRVPacket) {
        CoroutineScope(Dispatchers.Main).launch {
            val submittable = listOf(
                TypeDetailRVItem.TypeDetailRVItemSummary(typeRVPacket),
                TypeDetailRVItem.TypeDetailRVItemCategories(typeRVPacket)
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
            is TypeDetailRVItem.TypeDetailRVItemSummary -> SUMMARY
            is TypeDetailRVItem.TypeDetailRVItemCategories -> CATEGORIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> TypeDetailSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            CATEGORIES -> TypeDetailCategoriesViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TypeDetailSummaryViewHolder -> {
                val data =
                    (getItem(position) as TypeDetailRVItem.TypeDetailRVItemSummary).typeRVPacket
                holder.rebind(data, pieCentreClickListener)
            }
            is TypeDetailCategoriesViewHolder -> {
                val data =
                    (getItem(position) as TypeDetailRVItem.TypeDetailRVItemCategories).typeRVPacket
                holder.rebind(data, itemOnClickListener)
            }
        }
    }

    class TypeDetailSummaryViewHolder private constructor(private val binding: RvItemTypeDetailSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(newPacket: TypeRVPacket, pieCentreClickListener: GenericOnClickListener) {
            binding.packet = newPacket
            binding.pieCentreClickListener = pieCentreClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TypeDetailSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemTypeDetailSummaryBinding.inflate(layoutInflater, parent, false)
                return TypeDetailSummaryViewHolder(binding)
            }
        }
    }

    class TypeDetailCategoriesViewHolder private constructor(private val binding: RvItemTypeDetailCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            newPacket: TypeRVPacket,
            itemOnClickListener: TypeDetailOnClickListener
        ) {
            binding.packet = newPacket
            binding.onClickListener = itemOnClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TypeDetailCategoriesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RvItemTypeDetailCategoriesBinding.inflate(layoutInflater, parent, false)
                return TypeDetailCategoriesViewHolder(binding)
            }
        }
    }
}

class TypeDetailRVItemDiffCallback : DiffUtil.ItemCallback<TypeDetailRVItem>() {
    override fun areItemsTheSame(
        oldItemTypeDetail: TypeDetailRVItem,
        newItemTypeDetail: TypeDetailRVItem
    ): Boolean {
        return oldItemTypeDetail.rvItemId == newItemTypeDetail.rvItemId
    }

    override fun areContentsTheSame(
        oldItemTypeDetail: TypeDetailRVItem,
        newItemTypeDetail: TypeDetailRVItem
    ): Boolean {
        return oldItemTypeDetail == newItemTypeDetail
    }
}

class TypeDetailOnClickListener(val clickListener: (category: String) -> Unit) {
    fun onClick(category: String) = clickListener(category)
}

// Can reuse
//class GenericOnClickListener(val clickListener: () -> Unit) {
//    fun onClick() = clickListener()
//}

sealed class TypeDetailRVItem {
    abstract val rvItemId: Int

    data class TypeDetailRVItemSummary(val typeRVPacket: TypeRVPacket) : TypeDetailRVItem() {
        override val rvItemId: Int = 0
    }

    data class TypeDetailRVItemCategories(val typeRVPacket: TypeRVPacket) :
        TypeDetailRVItem() {
        override val rvItemId: Int = 1
    }

}

data class TypeRVPacket(
    val summaryPieData: PieData?,
    val summaryLegendLLData: List<TypeLegendLLData>,
    val categoriesMonthString: String,
    val categoriesShowMonthCurrency: Boolean,
    val categoriesMonthCurrency: String,
    val categoriesMonthAmount: String,
    val detailLLData: List<TypeCategoryLLData>
)

data class TypeLegendLLData(
    val colour: Int,
    val categoryName: String,
    val categoryPercent: String
)

data class TypeCategoryLLData(
    val iconDetail: IconDetail,
    val categoryName: String,
    val categoryPercent: String,
    val showCurrency: Boolean,
    val currency: String,
    val categoryAmount: String,
    val barData: BarData
)

