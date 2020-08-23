package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.userrepository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {

    private val userRepository by lazy { UserRepository.getInstance() }
    internal val categoriesSize = Transformations.map(userRepository.categories) { categories ->
        Pair(
            categories.filter { it.type == "Income" }.size,
            categories.filter { it.type == "Expenses" }.size
        )
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Batch delete categories
     */
    ////////////////////////////////////////////////////////////////////////////////

    internal fun deleteCategories(idList: List<Int>) {
        viewModelScope.launch(Dispatchers.Default) {
            userRepository.deleteCategoriesTransactionSuspend(idList)
        }
    }
}