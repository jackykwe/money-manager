package com.kaeonx.moneymanager.fragments.accounts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaeonx.moneymanager.databinding.RvItemAccountsDisplayBinding
import com.kaeonx.moneymanager.userrepository.domain.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class AccountsDisplayRVAdapter(
    private val editable: Boolean,
    private val itemOnClickListener: AccountOnClickListener
) : ListAdapter<Account, AccountsDisplayRVAdapter.AccountViewHolder>(AccountDiffCallback()) {

    fun submitListAndAddTailIfNecessary(list: List<Account>) {
        if (!editable) submitList(list) else {
            CoroutineScope(Dispatchers.Default).launch {
                val submittable = list + listOf(Account(null, "Add...", "TRANSPARENT"))
                withContext(Dispatchers.Main) {
                    submitList(submittable)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder.inflateAndCreateViewHolderFrom(parent)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.rebind(getItem(position), itemOnClickListener)
    }

    class AccountViewHolder private constructor(private val binding: RvItemAccountsDisplayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun rebind(account: Account, itemOnClickListener: AccountOnClickListener) {
            binding.account = account
            binding.onClickListener = itemOnClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun inflateAndCreateViewHolderFrom(parent: ViewGroup): AccountViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvItemAccountsDisplayBinding.inflate(layoutInflater, parent, false)
                return AccountViewHolder(binding)
            }
        }
    }
}

class AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
    override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem.accountId == newItem.accountId
    }

    override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem == newItem
    }
}

class AccountOnClickListener(val clickListener: (account: Account) -> Unit) : Serializable {
    fun onClick(account: Account) = clickListener(account)
}