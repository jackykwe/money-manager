package com.kaeonx.moneymanager.fragments.budget

import android.text.SpannedString
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kaeonx.moneymanager.userrepository.UserRepository

class BudgetViewModel : ViewModel() {

    private val userRepository = UserRepository.getInstance()

    internal var addOptions: Array<SpannedString>? = null
        private set

    private val _budgets = userRepository.getAllBudgets()
    val budgets = Transformations.map(_budgets) { result ->
        val categoriesWithBudget = _budgets.value!!.map { SpannedString(it.category) }
        addOptions = if (categoriesWithBudget.contains(SpannedString("Overall"))) {
            ArrayList(userRepository.categories.value!!
                .filter { it.type == "Expenses" }
                .map { SpannedString(it.name) })
                .apply {
                    removeAll(categoriesWithBudget)
                }.toTypedArray()
        } else {
            arrayListOf(buildSpannedString { bold { append("Overall") } })
                .apply {
                    addAll(userRepository.categories.value!!
                        .filter { it.type == "Expenses" }
                        .map { SpannedString(it.name) }
                    )
                    removeAll(categoriesWithBudget)
                }.toTypedArray()
        }
        result
    }
}

class BudgetOnClickListener(val clickListener: (category: String) -> Unit) {
    fun onClick(category: String) = clickListener(category)
}