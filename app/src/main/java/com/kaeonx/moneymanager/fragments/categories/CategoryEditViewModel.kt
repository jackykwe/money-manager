package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.CATEGORY_NAME_MAX_LENGTH
import com.kaeonx.moneymanager.userrepository.domain.Category
import kotlinx.coroutines.launch

class CategoryEditViewModel(private val oldCategory: Category) : ViewModel() {

    init {
        if (oldCategory.name == "Add…") {
            oldCategory.apply {
                name = ""
                iconHex = "F02D6"
                colourFamily = "Red"
            }
        }
    }

    private val userRepository = UserRepository.getInstance()
    private val otherCategoryNames = userRepository.categories.value!!
        .filter { it.type == oldCategory.type && it.name != oldCategory.name }
        .map { it.name } // TODO: ASYNC

    private var _currentCategory = MutableLiveData2(oldCategory.copy())
    val currentCategory: LiveData<Category>
        get() = _currentCategory

    fun changesWereMade(): Boolean {
        return oldCategory != _currentCategory.value
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Spinners
     * Note that if you edit any of the code inside here, please do the same
     * for AccountEditViewModel.
     */
    ////////////////////////////////////////////////////////////////////////////////

    val colourFamilySpinnerText = MutableLiveData<String>(_currentCategory.value.colourFamily)
    val colourFamilySpinnerError: LiveData<String?> = Transformations.map(colourFamilySpinnerText) {
        if (it in ColourHandler.getColourFamilies()) {
            _currentCategory.value = _currentCategory.value.copy(
                colourFamily = colourFamilySpinnerText.value!!
            )
            null
        } else {
            "ERROR. Please report this bug."
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Submit Checks
     */
    ////////////////////////////////////////////////////////////////////////////////

    // Two-way binding; cannot use MutableLiveData2
    val categoryNameETText = MutableLiveData<String>(_currentCategory.value.name)
    val categoryNameETError = Transformations.map(categoryNameETText) {
        val trimmed = it.trim()
        if (trimmed.length > CATEGORY_NAME_MAX_LENGTH) throw IllegalStateException("Category name exceeded $CATEGORY_NAME_MAX_LENGTH character limit")
        when {
            trimmed.isBlank() -> "Category Name must not be empty"
            trimmed in listOf("Add…", "(multiple)", "Overall") -> "This Category Name is reserved"
            otherCategoryNames.contains(trimmed) -> "This Category Name already exists"
            else -> {
                _currentCategory.value = _currentCategory.value.copy(name = trimmed)
                null
            }
        }
    }

    val iconHexETText =
        MutableLiveData<String>(_currentCategory.value.iconHex)  // Two-way binding; cannot use MutableLiveData2
    val iconHexETError = Transformations.map(iconHexETText) {
        val trimmed = it.trim()
        val errorText = when {
            trimmed.isBlank() || !trimmed.startsWith("F") -> {
                "Icon ID must start with \"F\""
            }
            trimmed == "F02D6" -> "This Icon ID is reserved"
            !IconHandler.iconHexIsValid(trimmed) -> "This Icon ID is invalid"
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
            colourFamilySpinnerError.value != null -> _showSnackBarText.value = "Invalid Colour"
            else -> {
                if (changesWereMade()) {
                    viewModelScope.launch {
                        userRepository.upsertCategoryTransactionSuspend(
                            newCategory = _currentCategory.value,
                            oldCategoryName = oldCategory.name
                        )
                        _navigateUp.value = true
                    }
                } else {
                    _navigateUp.value = true
                }
            }
        }
    }

    fun deleteOldCategory() {
        viewModelScope.launch {
            userRepository.deleteCategorySuspend(oldCategory)
            _navigateUp.value = true
        }
    }

    private val _showSnackBarText = MutableLiveData2<String?>(null)
    val showSnackBarText: LiveData<String?>
        get() = _showSnackBarText

    fun snackBarShown() {
        _showSnackBarText.value = null
    }

    private val _navigateUp = MutableLiveData2(false)
    val navigateUp: LiveData<Boolean>
        get() = _navigateUp

    fun navigateUpHandled() {
        _navigateUp.value = false
    }
}