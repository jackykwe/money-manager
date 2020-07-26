package com.kaeonx.moneymanager.fragments.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.databinding.RvItemTypeDisplayBinding
import com.kaeonx.moneymanager.userrepository.domain.Category

class TypeDisplayRVAdapter(
    private val type: String,
    private val editable: Boolean,
    private val itemOnClickListener: CategoryOnClickListener
) : ListAdapter<Category, TypeDisplayRVAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun getItemCount(): Int = if (editable) currentList.size + 1 else currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.inflateAndCreateViewHolderFrom(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        when (position) {
            currentList.size -> holder.rebind(Category(type, "Add...", "F065D", "Red,500"), itemOnClickListener)
            else -> holder.rebind(getItem(position), itemOnClickListener)
        }
    }

    class CategoryViewHolder private constructor(private val binding: RvItemTypeDisplayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun rebind(category: Category, itemOnClickListener: CategoryOnClickListener) {
            binding.category = category
            binding.onClickListener = itemOnClickListener

            binding.categoryIconInclude.iconBG.visibility = if (binding.category!!.name == "Add...") View.INVISIBLE else View.VISIBLE

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

class CategoryOnClickListener(val clickListener: (type: String, category: Category) -> Unit) {
    fun onClick(type: String, category: Category) = clickListener(type, category)
}