package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Category
import kotlinx.coroutines.launch

private const val TAG = "cevm"

class CategoryEditViewModel(private val oldCategory: Category) : ViewModel() {

    init {
        if (oldCategory.name == "Add...") {
            oldCategory.apply {
                name = ""
                iconHex = "F02D6"
            }
        }
    }

    private val userRepository = UserRepository.getInstance()
    private val otherCategoryNames = userRepository.categories.value!!.filter { it.type == oldCategory.type && it.name != oldCategory.name }.map { it.name } // TODO: ASYNC

    private var _currentCategory = MutableLiveData2(oldCategory.copy())
    val currentCategory: LiveData<Category>
        get() = _currentCategory
    fun changesWereMade(): Boolean {
        return oldCategory != _currentCategory.value
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Submit Checks
     */
    ////////////////////////////////////////////////////////////////////////////////

    val categoryNameETText = MutableLiveData<String>(_currentCategory.value.name)  // Two-way binding; cannot use MutableLiveData2
    val categoryNameETError = Transformations.map(categoryNameETText) {
        val trimmed = it.trim()
        when {
            trimmed.isBlank() -> "Category Name must not be empty"
            trimmed == "Add..." -> "Category Name must not be \"Add...\""
            otherCategoryNames.contains(trimmed) -> "This Category Name already exists"
            else -> {
                _currentCategory.value = _currentCategory.value.copy(name = trimmed)
                null
            }
        }
    }

    val iconHexETText = MutableLiveData<String>(_currentCategory.value.iconHex)  // Two-way binding; cannot use MutableLiveData2
    val iconHexETError = Transformations.map(iconHexETText) {
        val trimmed = it.trim()
//        _currentCategory.value = _currentCategory.value.copy(iconHex = "F0101")
//        _currentCategory.value = _currentCategory.value.copy(iconHex = it)
//        _currentCategory.value = _currentCategory.value.copy(iconHex = "F0202")
        val errorText = when {
            trimmed.isBlank() || !trimmed.startsWith("F") -> { "Icon ID must start with \"F\"" }
            trimmed == "F02D6" -> "This Icon ID is reserved."
            !IconHandler.iconHexIsValid(it) && it != "F02D6" -> "This Icon ID is invalid."
            else -> null
        }
        _currentCategory.value = _currentCategory.value.copy(
            iconHex = if (errorText == null) it else "F02D6"
        )
        errorText
    }

    fun saveBTClicked() {
        when {
            categoryNameETError.value != null -> _showSnackBarText.value = "Invalid Category Name"
            iconHexETError.value != null -> _showSnackBarText.value = "Invalid Icon ID"
            else -> _showSnackBarText.value = "OK U GOOD" // SAVE
        }
    }

    fun deleteOldCategory() {
        viewModelScope.launch {
            userRepository.deleteCategory(oldCategory)
        }
    }

    private val _showSnackBarText = MutableLiveData2<String?>(null)
    val showSnackBarText : LiveData<String?>
        get() = _showSnackBarText
    fun snackBarShown() {
        _showSnackBarText.value = null
    }

//
//    fun addCategory(transaction: Transaction) {
//        Log.d(TAG, "addCategory: called")
//        viewModelScope.launch {
//            userRepository.addCategory(transaction)
//        }
//    }





//    private val liveDataActivator = Observer<Any> { }

//    init {
//        memoIsNullOrBlank.observeForever(liveDataActivator)
//    }
//
//    override fun onCleared() {
//        memoIsNullOrBlank.removeObserver(liveDataActivator)
//        super.onCleared()
//    }

}