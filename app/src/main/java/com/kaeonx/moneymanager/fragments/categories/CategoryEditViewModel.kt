package com.kaeonx.moneymanager.fragments.categories

import android.util.Log
import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Category
import kotlinx.coroutines.launch

private const val TAG = "cevm"

class CategoryEditViewModel(private val oldCategory: Category) : ViewModel() {

    private val liveDataActivator = Observer<Any> {
        Log.d(TAG, "It got updated! $it")
    }

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
    init {
        _currentCategory.observeForever(liveDataActivator)
    }
    val currentCategory: LiveData<Category>
        get() = _currentCategory
    fun changesWereMade(): Boolean {
        return oldCategory != _currentCategory.value
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Spinners
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
            trimmed == "Add..." -> "This Category Name is reserved"
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
            !IconHandler.iconHexIsValid(it) && it != "F02D6" -> "This Icon ID is invalid"
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
            else -> {
                if (changesWereMade()) {
                    viewModelScope.launch {
                        userRepository.upsertCategory(_currentCategory.value)
                        _navigateUp.value = true
                    }
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

//
//    fun addCategory(transaction: Transaction) {
//        Log.d(TAG, "addCategory: called")
//        viewModelScope.launch {
//            userRepository.addCategory(transaction)
//        }
//    }



//    init {
//        memoIsNullOrBlank.observeForever(liveDataActivator)
//    }
//
    override fun onCleared() {
        _currentCategory.removeObserver(liveDataActivator)
        super.onCleared()
    }

}

