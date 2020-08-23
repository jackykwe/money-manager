package com.kaeonx.moneymanager.fragments.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.databinding.RvItemTypeDisplayBinding
import com.kaeonx.moneymanager.userrepository.domain.Category
import kotlinx.coroutines.*

class TypeDisplayRVAdapter(
    private val type: String,
    private val editable: Boolean,
    private val itemOnClickListener: CategoryOnClickListener,
    private val itemOnLongClickListener: CategoryOnClickListener
) : ListAdapter<Category, TypeDisplayRVAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    fun submitListAndAddTailIfNecessary(list: List<Category>) {
        if (!editable) {
            submitList(list)  // for CategoriesDF, launched from TransactionsBSDF
        } else {
            CoroutineScope(Dispatchers.Default).launch {
                val submittable =
                    list + listOf(Category(null, type, "Add…", "F065D", "TRANSPARENT"))
                withContext(Dispatchers.Main) {
                    delay(150L)
                    submitList(submittable)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.inflateAndCreateViewHolderFrom(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.rebind(getItem(position), itemOnClickListener, itemOnLongClickListener)
    }

    class CategoryViewHolder private constructor(private val binding: RvItemTypeDisplayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rebind(
            category: Category,
            itemOnClickListener: CategoryOnClickListener,
            itemOnLongClickListener: CategoryOnClickListener
        ) {
            binding.category = category
            binding.onClickListener = itemOnClickListener
            binding.categoryLL.setOnLongClickListener { view ->
                itemOnLongClickListener.onClick(view, category)
                true
            }
            binding.executePendingBindings()
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
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}

class CategoryOnClickListener(val clickListener: (view: View, category: Category) -> Unit) {
    fun onClick(view: View, category: Category) = clickListener(view, category)
}