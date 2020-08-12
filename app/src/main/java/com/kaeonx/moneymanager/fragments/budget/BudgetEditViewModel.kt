package com.kaeonx.moneymanager.fragments.budget

import androidx.lifecycle.*
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Budget
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class BudgetEditViewModel(private val oldBudget: Budget) : ViewModel() {

    private val userRepository = UserRepository.getInstance()

    private val _currentBudget = MutableLiveData2(oldBudget.copy())
    val currentBudget: LiveData<Budget>
        get() = _currentBudget

    internal fun changesWereMade(): Boolean = _currentBudget.value != oldBudget

    private val entryArray = App.context.resources.getStringArray(R.array.ccc_currencies_entries)
    private val valueArray = App.context.resources.getStringArray(R.array.ccc_currencies_values)
    private fun valueToEntry(value: String): String = entryArray[valueArray.indexOf(value)]
    private fun entryToValue(entry: String): String = valueArray[entryArray.indexOf(entry)]

    val currencySpinnerText = MutableLiveData<String>(
        valueToEntry(_currentBudget.value.originalCurrency)
    )
    val currencySpinnerError = Transformations.map(currencySpinnerText) {
        amountETText.value = amountETText.value  // to update amountETHelperText
        // it (currencySpinnerText.value) is ENTRY form. You need to run entryToValue(it)
        // to get the 3 character currency code.
        if (it.isNullOrBlank()) "Please report this bug."
        else {
            _currentBudget.value = _currentBudget.value.copy(
                originalCurrency = entryToValue(it)
            )
            null
        }
    }

    val amountETText = MutableLiveData<String>(_currentBudget.value.originalAmount)
    val amountETHelperText = Transformations.map(amountETText) {
        when {
            it.isNullOrBlank() -> null
            else -> {
                when (val display = CurrencyHandler.displayAmount(BigDecimal(it))) {
                    "0" -> null
                    else -> {
                        _currentBudget.value = _currentBudget.value.copy(
                            originalAmount = display
                        )
                        val currency = entryToValue(
                            currencySpinnerText.value
                                ?: throw IllegalStateException("currencySpinnerText shouldn't be null")
                        )
                        "Will be saved as $currency $display"
                    }
                }

            }
        }
    }
    val amountETError = Transformations.map(amountETText) {
        when {
            it.isNullOrEmpty() -> "Monthly Budget cannot be empty"
            else -> {
                when (val display = CurrencyHandler.displayAmount(BigDecimal(it))) {
                    "0" -> "Monthly Budget must be positive"
                    else -> null
                }
            }
        }
    }

    private suspend fun generateAmountText(startMillis: Long, endMillis: Long): String =
        userRepository.getTransactionsBetweenSuspend(
            type = "Expenses",
            category = _currentBudget.value.category,
            startMillis = startMillis,
            endMillis = endMillis
        )
            .run {
                var rangeAmount = BigDecimal.ZERO
                forEach {
                    rangeAmount = rangeAmount.plus(
                        if (it.originalCurrency == _currentBudget.value.originalCurrency) {
                            BigDecimal(it.originalAmount)
                        } else {
                            CurrencyHandler.convertAmount(
                                BigDecimal(it.originalAmount),
                                it.originalCurrency,
                                _currentBudget.value.originalCurrency
                            )
                        }
                    )
                }
                CurrencyHandler.displayAmount(rangeAmount)
            }

    internal suspend fun generateQuickBudgetItems(): List<QuickBudgetDialogItem> {
        val cal = Calendar.getInstance()
        val currentMonthString: String
        val currentMonthStartMillis: Long
        val currentMonthEndMillis: Long
        cal.run {
            currentMonthString = CalendarHandler.getFormattedString(
                clone() as Calendar,
                "MMM yyyy"
            ).toUpperCase(Locale.ROOT)
            currentMonthStartMillis = CalendarHandler.getStartOfMonthMillis(clone() as Calendar)
            currentMonthEndMillis = CalendarHandler.getEndOfMonthMillis(clone() as Calendar)
        }
        cal.add(Calendar.MONTH, -1)
        val previousMonthString: String
        val previousMonthStartMillis: Long
        val previousMonthEndMillis: Long
        cal.run {
            previousMonthString = CalendarHandler.getFormattedString(
                clone() as Calendar,
                "MMM yyyy"
            ).toUpperCase(Locale.ROOT)
            previousMonthStartMillis =
                CalendarHandler.getStartOfMonthMillis(clone() as Calendar)
            previousMonthEndMillis = CalendarHandler.getEndOfMonthMillis(clone() as Calendar)
        }

        return listOf(
            QuickBudgetDialogItem(
                mainTitleText = "Last ${_currentBudget.value.category} Expenses",
                monthText = "($previousMonthString)",
                currencyText = _currentBudget.value.originalCurrency,
                amountText = generateAmountText(
                    previousMonthStartMillis,
                    previousMonthEndMillis
                )
                    .also { cache[0] = it }
            ),
            QuickBudgetDialogItem(
                mainTitleText = "Current ${_currentBudget.value.category} Expenses",
                monthText = "($currentMonthString)",
                currencyText = _currentBudget.value.originalCurrency,
                amountText = generateAmountText(currentMonthStartMillis, currentMonthEndMillis)
                    .also { cache[1] = it }
            )
        )
    }

    private val cache = Array(2) { _ -> "" }
    internal fun selectedIndex(index: Int) {
        if (index == -1) {
            cache[0] = ""
            cache[1] = ""
        } else {
            amountETText.value = cache[index]
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Submit Checks
     */
    ////////////////////////////////////////////////////////////////////////////////

    internal fun saveBTClicked() {
        when {
            currencySpinnerError.value != null -> _showSnackBarText.value =
                "Please report this bug."
            amountETError.value != null -> _showSnackBarText.value = "Invalid Monthly Budget"
            else -> {
                if (changesWereMade()) {
                    viewModelScope.launch {
                        userRepository.upsertBudget(_currentBudget.value)
                        _navigateUp.value = true
                    }
                } else {
                    _navigateUp.value = true
                }
            }
        }
    }

    internal fun deleteOldBudget() {
        viewModelScope.launch {
            userRepository.deleteBudget(oldBudget)
            _navigateUpTwoSteps.value = true
        }
    }

    private val _showSnackBarText = MutableLiveData2<String?>(null)
    internal val showSnackBarText: LiveData<String?>
        get() = _showSnackBarText

    internal fun snackBarShown() {
        _showSnackBarText.value = null
    }

    private val _navigateUp = MutableLiveData2(false)
    internal val navigateUp: LiveData<Boolean>
        get() = _navigateUp

    internal fun navigateUpHandled() {
        _navigateUp.value = false
    }

    private val _navigateUpTwoSteps = MutableLiveData2(false)
    internal val navigateUpTwoSteps: LiveData<Boolean>
        get() = _navigateUpTwoSteps

    internal fun navigateUpTwoStepsHandled() {
        _navigateUpTwoSteps.value = false
    }

}