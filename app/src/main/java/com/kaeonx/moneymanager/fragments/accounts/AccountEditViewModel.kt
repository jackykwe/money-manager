package com.kaeonx.moneymanager.fragments.accounts

import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Account
import kotlinx.coroutines.launch

class AccountEditViewModel(private val oldAccount: Account) : ViewModel() {

    init {
        if (oldAccount.name == "Add…") {
            oldAccount.apply {
                name = ""
                colourString = "Red"
            }
        }
    }

    private val userRepository = UserRepository.getInstance()
    private val otherAccountNames =
        userRepository.accounts.value!!.filter { it.name != oldAccount.name }
            .map { it.name } // TODO: ASYNC

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

    val colourFamilySpinnerText = MutableLiveData<String>(_currentAccount.value.colourString)
    val colourFamilySpinnerError: LiveData<String?> = Transformations.map(colourFamilySpinnerText) {
        if (it in ColourHandler.getColourFamilies()) {
            _currentAccount.value = _currentAccount.value.copy(
                colourString = colourFamilySpinnerText.value!!
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
    val accountNameETText = MutableLiveData<String>(_currentAccount.value.name)
    val accountNameETError = Transformations.map(accountNameETText) {
        val trimmed = it.trim()
        when {
            trimmed.isBlank() -> "Account Name must not be empty"
            trimmed == "Add…" -> "This Account Name is reserved"
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
            else -> {
                if (changesWereMade()) {
                    viewModelScope.launch {
                        // TODO: MAKE THESE INTO A TRANSACTION. EITHER ALL PASS OR ALL FAIL. NO HALFWAY.
                        // TODO ^ might become buggy if this continues (though you can always rerun it
                        // TODO: and it'll correct itself)
                        userRepository.upsertAccount(_currentAccount.value)
                        userRepository.updateTransactionsRenameAccount(
                            oldAccountName = oldAccount.name,
                            newAccountName = _currentAccount.value.name
                        )
                        if (UserPDS.getString("tst_default_account") == oldAccount.name) {
                            UserPDS.putString("tst_default_account", _currentAccount.value.name)
                        }
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
            if (UserPDS.getString("tst_default_account") == oldAccount.name) {
                UserPDS.putString(
                    "tst_default_account",
                    userRepository.accounts.value!!.find { it.name != oldAccount.name }!!.name
                    // The .find check is there in case the LiveData hasn't been updated yet.
                )
            }
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