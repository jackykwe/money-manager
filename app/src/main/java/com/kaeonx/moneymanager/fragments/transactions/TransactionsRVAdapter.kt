package com.kaeonx.moneymanager.fragments.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.databinding.RvItemTransactionsDayBinding
import com.kaeonx.moneymanager.databinding.RvItemTransactionsSummaryBinding
import com.kaeonx.moneymanager.databinding.RvLlItemTransactionBinding
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SUMMARY = 0
private const val DAY_TRANSACTIONS = 1
private const val TAG = "trva"

class TransactionsRVAdapter(private val itemOnClickListener: TransactionOnClickListener) :
    ListAdapter<RVItem, RecyclerView.ViewHolder>(RVItemDiffCallback()) {

    fun submitListAndAddHeader(list: List<DayTransactions>) {
        CoroutineScope(Dispatchers.Default).launch {
            val submittable = when {
                list.isEmpty() -> listOf(RVItem.RVItemHeader)
                else -> listOf(RVItem.RVItemHeader) + list.map { RVItem.RVItemDayTransactions(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(submittable)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RVItem.RVItemHeader -> SUMMARY
            is RVItem.RVItemDayTransactions -> DAY_TRANSACTIONS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SUMMARY -> TransactionsSummaryViewHolder.inflateAndCreateViewHolderFrom(parent)
            DAY_TRANSACTIONS -> TransactionsDayViewHolder.inflateAndCreateViewHolderFrom(parent)
            else -> throw IllegalArgumentException("Illegal viewType: ${viewType}. viewType must be either 0 or 1.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionsDayViewHolder -> {
                val item = (getItem(position) as RVItem.RVItemDayTransactions).dayTransactions
                holder.rebind(item, itemOnClickListener)
            }
            is TransactionsSummaryViewHolder -> { }
        }
    }

    class TransactionsDayViewHolder private constructor(private val binding: RvItemTransactionsDayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun rebind(item: DayTransactions, itemOnClickListener: TransactionOnClickListener) {
            binding.dayTransactions = item
            binding.executePendingBindings()
            // It is always a good idea to execute pending bindings when using binding adapters
            // in a RecyclerView, since it can be slightly faster to size the views.

            binding.dayTransactionsLL.removeAllViews()
            val layoutInflater = LayoutInflater.from(binding.dayTransactionsLL.context)
            for (transaction in item.transactions) {
                val itemBinding = RvLlItemTransactionBinding.inflate(layoutInflater, null, false)
                itemBinding.transaction = transaction
                itemBinding.onClickListener = itemOnClickListener
                itemBinding.executePendingBindings()
                binding.dayTransactionsLL.addView(itemBinding.root)

//                val category = CategoryIconHandler.getCategory(context, firebaseViewModel.currentUserLD.value!!.uid, transaction.type, transaction.category)
//                itemBinding.iconBG.drawable.setTint(ColourHandler.getColourObject(context.resources, category.colourString))
                itemBinding.categoryIconInclude.iconTV.text = IconHandler.getDisplayHex("F0011")  //CategoryIconHandler.hexToIcon(category.iconHex)

//                val account = AccountHandler.getAccount(context, firebaseViewModel.currentUserLD.value!!.uid, transaction.account)
//                itemBinding.iconRing.drawable.setTint(ColourHandler.getColourObject(context.resources, account.colourString))
//                itemBinding.setOnClickListener {
//                    fragment.findNavController().navigate(RootTransactionsFragmentDirections.actionRootTransactionsFragmentToRootTransactionEditFragment(transaction))
//                }
            }
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsDayViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
//                val view = layoutInflater.inflate(R.layout.rv_item_transactions_day, parent, false)
                val binding = RvItemTransactionsDayBinding.inflate(layoutInflater, parent, false)
                return TransactionsDayViewHolder(binding)
            }
        }
    }

    class TransactionsSummaryViewHolder private constructor(binding: RvItemTransactionsSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
//    var summaryMonthTV: TextView = containerView.findViewById(R.id.summaryMonthTV)
//    var summaryLeftArrowTV: TextView = containerView.findViewById(R.id.summaryLeftArrowTV)
//    var summaryRightArrowTV: TextView = containerView.findViewById(R.id.summaryRightArrowTV)

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): TransactionsSummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemTransactionsSummaryBinding.inflate(layoutInflater, parent, false)
                return TransactionsSummaryViewHolder(binding)
            }
        }
    }
}

class RVItemDiffCallback : DiffUtil.ItemCallback<RVItem>() {
    override fun areItemsTheSame(oldItem: RVItem, newItem: RVItem): Boolean {
        return oldItem.rvItemId == newItem.rvItemId
    }

    override fun areContentsTheSame(oldItem: RVItem, newItem: RVItem): Boolean {
        return oldItem == newItem
    }
}

class TransactionOnClickListener(val clickListener: (transaction: Transaction) -> Unit) {
    fun onClick(transaction: Transaction) = clickListener(transaction)
}

sealed class RVItem {
    data class RVItemDayTransactions(val dayTransactions: DayTransactions) : RVItem() {
        override val rvItemId: Long = dayTransactions.dayOfMonth
    }

    object RVItemHeader : RVItem() {
        override val rvItemId: Long = Long.MIN_VALUE
    }

    abstract val rvItemId: Long
}