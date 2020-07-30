package com.kaeonx.moneymanager.fragments.accounts

import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Account
import kotlinx.coroutines.launch

class AccountEditViewModel(private val oldAccount: Account) : ViewModel() {

    init {
        if (oldAccount.name == "Add...") {
            oldAccount.apply {
                name = ""
                colourString = "Red,500"
            }
        }
    }

    private val userRepository = UserRepository.getInstance()
    private val otherAccountNames = userRepository.accounts.value!!.filter { it.name != oldAccount.name }.map { it.name } // TODO: ASYNC

    private var _currentAccount = MutableLiveData2(oldAccount.copy())
    val currentAccount: LiveData<Account>
        get() = _currentAccount
    fun changesWereMade(): Boolean {
        return oldAccount != _currentAccount.value
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Spinners
     * Note that if you edit any of the code inside here, please do the same
     * for the CategoryEditViewModel (maybe make this into a class?) //TODO
     */
    ////////////////////////////////////////////////////////////////////////////////

    var colourFamilies = MutableLiveData2(ColourHandler.getColourFamiliesFull())
    var colourIntensities = MutableLiveData2(ColourHandler.getColourIntensitiesFull())

    val colourFamilySpinnerText = MutableLiveData<String>(ColourHandler.readColourFamily(_currentAccount.value.colourString))
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

    var colourIntensitySpinnerText = MutableLiveData(ColourHandler.readColourIntensity(_currentAccount.value.colourString))
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
        _currentAccount.value = _currentAccount.value.copy(
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

    val accountNameETText = MutableLiveData<String>(_currentAccount.value.name)  // Two-way binding; cannot use MutableLiveData2
    val accountNameETError = Transformations.map(accountNameETText) {
        val trimmed = it.trim()
        when {
            trimmed.isBlank() -> "Account Name must not be empty"
            trimmed == "Add..." -> "This Account Name is reserved"
            otherAccountNames.contains(trimmed) -> "This Account Name already exists"
            else -> {
                _currentAccount.value = _currentAccount.value.copy(name = trimmed)
                null
            }
        }
    }

    fun saveBTClicked() {
        when {
            accountNameETError.value != null -> _showSnackBarText.value = "Invalid Account Name"
            colourFamilySpinnerError.value != null -> _showSnackBarText.value = "Invalid Colour"
            colourIntensitySpinnerError.value != null -> _showSnackBarText.value = "Invalid Colour Intensity"
            else -> {
                if (changesWereMade()) {
                    viewModelScope.launch {
                        userRepository.upsertAccount(_currentAccount.value)
                        _navigateUp.value = true
                    }
                } else {
                    _navigateUp.value = true
                }
            }
        }
    }

    fun deleteOldAccount() {
        viewModelScope.launch {
            userRepository.deleteAccount(oldAccount)
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