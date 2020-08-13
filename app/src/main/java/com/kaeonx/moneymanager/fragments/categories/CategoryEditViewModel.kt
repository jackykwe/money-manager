package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Category
import kotlinx.coroutines.launch

class CategoryEditViewModel(private val oldCategory: Category) : ViewModel() {

    init {
        if (oldCategory.name == "Add…") {
            oldCategory.apply {
                name = ""
                iconHex = "F02D6"
                colourString = "Red,500"
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
     * Spinners
     * Note that if you edit any of the code inside here, please do the same
     * for the AccountEditViewModel (maybe make this into a class?) //TODO
     */
    ////////////////////////////////////////////////////////////////////////////////

    var colourFamilies = MutableLiveData2(ColourHandler.getColourFamiliesFull())
    var colourIntensities = MutableLiveData2(ColourHandler.getColourIntensitiesFull())

    val colourFamilySpinnerText = MutableLiveData<String>(ColourHandler.readColourFamily(_currentCategory.value.colourString))
    val colourFamilySpinnerError: LiveData<String?> = Transformations.map(colourFamilySpinnerText) {
        when (it) {
            "Black", "White" -> {
                colourIntensitySpinnerText.value = null
                updateColours()
                null
            }
            "Blue Grey", "Brown", "Grey" -> {
                if (colourIntensitySpinnerText.value == null) { colourIntensitySpinnerText.value = "500" }
                colourIntensities.value = ColourHandler.getColourIntensitiesPartial()
                updateColours()
                null
            }
            "Red", "Deep Purple", "Light Blue", "Green", "Yellow",
            "Deep Orange", "Pink", "Indigo", "Cyan", "Light Green",
            "Amber", "Purple", "Blue", "Teal", "Lime",
            "Orange" -> {
                if (colourIntensitySpinnerText.value == null) { colourIntensitySpinnerText.value = "500" }
                colourIntensities.value = ColourHandler.getColourIntensitiesFull()
                updateColours()
                null
            }
            else -> {
                colourIntensities.value = ColourHandler.getColourIntensitiesFull()
                "Invalid Colour. Please select from the dropdown menu."
            }
        }
    }

    var colourIntensitySpinnerText = MutableLiveData(ColourHandler.readColourIntensity(_currentCategory.value.colourString))
    val colourIntensitySpinnerError = Transformations.map(colourIntensitySpinnerText) {
        when (it) {
            "A100", "A200", "A400", "A700" -> {
                colourFamilies.value = ColourHandler.getColourFamiliesPartial()
                if (colourFamilySpinnerText.value.toString() in listOf("Blue Grey", "Brown", "Grey")) {
                    "BUG! Please screenshot and report this bug. [A]"
                } else {
                    updateColours()
                    null
                }
            }
            null -> {
                colourFamilies.value = ColourHandler.getColourFamiliesFull()
                updateColours()
                null
            }
            "50", "100", "200", "300", "400",
            "500", "600", "700", "800", "900" -> {
                colourFamilies.value = ColourHandler.getColourFamiliesFull()
                if (colourFamilySpinnerText.value.toString() in listOf("Black", "White")) {
                    "BUG! Please screenshot and report this bug. [B]"
                } else {
                    updateColours()
                    null
                }
            }
            else -> {
                colourFamilies.value = ColourHandler.getColourFamiliesPartial()
                "Invalid Colour Intensity. Please select from the dropdown menu."
            }
        }
    }

    // Instead of hooking up a complicated set of LiveDatas, I'm just going to do this
    private fun updateColours() {
        _currentCategory.value = _currentCategory.value.copy(
            colourString = ColourHandler.saveColourString(
                colourFamilySpinnerText.value!!,
                colourIntensitySpinnerText.value
            )
        )
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
            trimmed in listOf("Add…", "(multiple)", "Overall") -> "This Category Name is reserved"
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
        val errorText = when {
            trimmed.isBlank() || !trimmed.startsWith("F") -> { "Icon ID must start with \"F\"" }
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
            colourIntensitySpinnerError.value != null -> _showSnackBarText.value = "Invalid Colour Intensity"
            else -> {
                if (changesWereMade()) {
                    viewModelScope.launch {
                        userRepository.upsertCategory(_currentCategory.value)
                        userRepository.updateTransactionsRenameCategory(
                            type = _currentCategory.value.type,
                            oldCategoryName = oldCategory.name,
                            newCategoryName = _currentCategory.value.name
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
            userRepository.deleteCategory(oldCategory)
            _navigateUp.value = true
        }
    }

    private val _showSnackBarText = MutableLiveData2<String?>(null)
    val showSnackBarText : LiveData<String?>
        get() = _showSnackBarText
    fun snackBarShown() {
        _showSnackBarText.value = null
    }
    private val _navigateUp = MutableLiveData2(false)
    val navigateUp : LiveData<Boolean>
        get() = _navigateUp
    fun navigateUpHandled() {
        _navigateUp.value = false
    }
}