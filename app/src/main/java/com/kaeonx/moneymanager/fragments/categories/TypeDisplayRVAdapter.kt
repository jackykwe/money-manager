package com.kaeonx.moneymanager.fragments.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.databinding.RvItemTypeDisplayBinding
import com.kaeonx.moneymanager.userrepository.domain.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class TypeDisplayRVAdapter(
    private val type: String,
    private val editable: Boolean,
    private val itemOnClickListener: CategoryOnClickListener
) : ListAdapter<Category, TypeDisplayRVAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    fun submitListAndAddTailIfNecessary(list: List<Category>) {
        if (!editable) submitList(list) else {
            CoroutineScope(Dispatchers.Default).launch {
                val submittable = list + listOf(Category(type, "Add...", "F065D", "Red,500"))
                withContext(Dispatchers.Main) {
                    submitList(submittable)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.inflateAndCreateViewHolderFrom(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.rebind(getItem(position), itemOnClickListener)
    }

    class CategoryViewHolder private constructor(private val binding: RvItemTypeDisplayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun rebind(category: Category, itemOnClickListener: CategoryOnClickListener) {
            binding.category = category
            binding.onClickListener = itemOnClickListener

            binding.categoryIconInclude.iconBG.visibility = if (category.name == "Add...") View.INVISIBLE else View.VISIBLE

            binding.executePendingBindings()
//            binding.categoryIconInclude.categoryIconBG.drawable.setTint(ColourHandler.getColourObject((tabLayoutControllerFragment as Fragment).resources, categoriesAL[position].colourString))
//            binding.categoryIconInclude.categoryIconTV.text = hexToIcon(categoriesAL[position].iconHex)
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): CategoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
//                val view = layoutInflater.inflate(R.layout.rv_item_transactions_day, parent, false)
                val binding = RvItemTypeDisplayBinding.inflate(layoutInflater, parent, false)
                binding.categoryIconInclude.iconRing.visibility = View.GONE
                return CategoryViewHolder(binding)

            }
        }

    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}

class CategoryOnClickListener(val clickListener: (category: Category) -> Unit) : Serializable {
    fun onClick(category: Category) = clickListener(category)
}