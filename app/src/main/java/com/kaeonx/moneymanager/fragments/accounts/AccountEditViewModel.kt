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
                colourFamily = "Red"
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

    val colourFamilySpinnerText = MutableLiveData<String>(_currentAccount.value.colourFamily)
    val colourFamilySpinnerError: LiveData<String?> = Transformations.map(colourFamilySpinnerText) {
        if (it in ColourHandler.getColourFamilies()) {
            _currentAccount.value = _currentAccount.value.copy(
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
                        userRepository.upsertAccountTransactionSuspend(
                            newAccount = _currentAccount.value,
                            oldAccountName = oldAccount.name,
                            updateTstDefaultAccount = oldAccount.name == UserPDS.getString("tst_default_account")
                        )
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
            userRepository.deleteAccountTransactionSuspend(
                account = oldAccount,
                updateTstDefaultAccount = oldAccount.name == UserPDS.getString("tst_default_account"),
                newTstDefaultAccount = userRepository.accounts.value!!.find { it.name != oldAccount.name }!!.name
            )
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