package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Account
import com.kaeonx.moneymanager.userrepository.domain.Category
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class TransactionsBSDFViewModel(private val oldTransaction: Transaction): ViewModel() {

    // Not initialised unless necessary
    private val initCalendar: Calendar by lazy {
        val c = Calendar.getInstance()
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND))
        c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND))
        c
    }

    init {
        if (oldTransaction.transactionId == null) {
            // New transaction
            oldTransaction.timestamp = initCalendar.timeInMillis
        }
    }

    private var _currentTransaction = MutableLiveData2(oldTransaction.copy())
    val currentTransaction: LiveData<Transaction>
        get() = _currentTransaction
    fun changesWereMade(): Boolean {
        return oldTransaction != _currentTransaction.value
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Arithmetic
     */
    ////////////////////////////////////////////////////////////////////////////////

    companion object {
        internal const val MAX_DP = 2  // Maximum number of digits after decimal point
        internal const val MAX_INT = 6  // Maximum number of digits before decimal point
    }

    enum class ErrorType {
        OVERFLOW,
        DIV_ZERO
    }

    private fun updateAmountTVText(): String {
        return when (_error.value) {
            null -> _currentTransaction.value.originalAmount
            ErrorType.OVERFLOW -> "e: overflow"
            ErrorType.DIV_ZERO -> "e: div zero"
        }
    }

    private val _pendingOperation = MutableLiveData2<String?>(null)
    val pendingOpTVText: LiveData<String?>
        get() = _pendingOperation

    // the only other possible values are valid values of _amountTVText
    private val _pendingAmtTVText = MutableLiveData2<String?>(null)
    val pendingAmtTVText: LiveData<String?>
        get() = _pendingAmtTVText

    private val _error = MutableLiveData2<ErrorType?>(null)
    val error: LiveData<ErrorType?>
        get() = _error
    val backspaceBTText = Transformations.map(_error) {
        when (it) {
            null -> IconHandler.getDisplayHex("F0B5C")  // BACKSPACE_OUTLINE
            else -> IconHandler.getDisplayHex("F006E")  // BACKSPACE_BLACK
        }
    }

    private val operand1IsZero = Transformations.map(_currentTransaction) {
        BigDecimal(it.originalAmount).compareTo(BigDecimal.ZERO) == 0
    }
    val amountTVText = MediatorLiveData<String>().apply {
        addSource(_currentTransaction) { value = updateAmountTVText() }
        addSource(_error) { value = updateAmountTVText() }
    }


    private fun determineNewText(oldValue: String?, newChar: String): String {
        return if (oldValue.isNullOrEmpty()) {
            if (newChar == ".") "0." else newChar  // Initialise value for _pendingAmtTVText
        } else if (oldValue == "0" && newChar != ".") {
            newChar  // Replaces 0 with 1~9
        } else if (oldValue.contains(".")) {  // If decimal
            if ((oldValue.substring(oldValue.indexOf(".") + 1).length < MAX_DP) && newChar != ".") {
                // If less than 2 dp && newChar is not .
                oldValue + newChar
            } else oldValue
        } else {  // If not decimal
            if (oldValue.length < MAX_INT || newChar == ".") {
                // Not allowed to add if length == 6 && newChar != "."
                oldValue + newChar
            } else oldValue
        }
    }

    fun digitDecimalPressed(digitDecimal: String) {
        if (_pendingOperation.value == null) {
            _currentTransaction.value = _currentTransaction.value.copy(
                originalAmount = determineNewText(_currentTransaction.value.originalAmount, digitDecimal)
            )
        } else {
            _pendingAmtTVText.value = determineNewText(_pendingAmtTVText.value, digitDecimal)
        }
    }

    // Arithmetic handled in here
    fun operatorPressed(operator: String) {
        var operand1 = BigDecimal(_currentTransaction.value.originalAmount)
        if (!_pendingAmtTVText.value.isNullOrEmpty()) {
            // Perform arithmetic
            val operand2 = BigDecimal(_pendingAmtTVText.value)
            when (_pendingOperation.value) {
                "+" -> operand1 = operand1.plus(operand2)
                "-" -> operand1 = operand1.minus(operand2)
                "×" -> operand1 = operand1.times(operand2)
                "÷" -> {
                    if (operand2.compareTo(BigDecimal.ZERO) == 0) {
                        _error.value = ErrorType.DIV_ZERO
                        return
                    } else {
                        operand1 = operand1.divide(
                            operand2,
                            9,
                            RoundingMode.HALF_UP
                        )  // div operator uses RoundingMode.HALF_EVEN
                    }
                }
                null -> throw IllegalStateException("_pendingAmtTVText is not null or empty, but _pendingOperation is null.")
                else -> throw IllegalArgumentException("Illegal pendingOperation: ${_pendingOperation.value}")
            }
            _pendingAmtTVText.value = null
        }

        // Must be done after arithmetic, as the old _pendingOperation.value is used in the arithmetic.
        _pendingOperation.value = if (operator == "=") null else operator

        // Check if there's overflow after arithmetic. If not, check if operand1 is zero
        if (operand1 >= BigDecimal("1E$MAX_INT")) {
            _error.value = ErrorType.OVERFLOW
        } else {
            _currentTransaction.value = _currentTransaction.value.copy(
                originalAmount = if (operand1 < BigDecimal.ZERO) {
                    _showToastText.value = "Negative values are not allowed"
                    "0"
                } else CurrencyHandler.displayAmount(operand1)
            )
        }
    }

    fun backspacePressed() {
        if (_error.value != null) {
            // There is an error
            backspaceLongPressed()
        } else {
            // There is no error
            if (_pendingOperation.value == null) {
                val text = _currentTransaction.value.originalAmount
                _currentTransaction.value = _currentTransaction.value.copy(
                    originalAmount = if (text.length == 1) "0" else text.substring(0, text.length - 1)
                )
            } else {
                val text = _pendingAmtTVText.value
                if (text.isNullOrEmpty()) {
                    _pendingOperation.value = null
                } else {
                    _pendingAmtTVText.value = text.substring(0, text.length - 1)
                }
            }
        }
    }

    fun backspaceLongPressed(): Boolean {
        // Reset everything (EXCEPT memo and category) to initial state
        _currentTransaction.value = _currentTransaction.value.copy(
            originalAmount = "0"
        )
        _pendingOperation.value = null
        _pendingAmtTVText.value = null
        _error.value = null
        return true
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Date & Time Manipulation
     */
    ////////////////////////////////////////////////////////////////////////////////

    fun updateCalendar(calendar: Calendar) {
        _currentTransaction.value = _currentTransaction.value.copy(
            timestamp = calendar.timeInMillis
        )
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Account and Category Manipulation
     */
    ////////////////////////////////////////////////////////////////////////////////

    fun updateAccount(newAccount: Account) {
        _currentTransaction.value = _currentTransaction.value.copy(
            account = newAccount.name
        )
    }

    fun updateCategory(newCategory: Category) {
        _currentTransaction.value = _currentTransaction.value.copy(
            type =  newCategory.type,
            category = newCategory.name
        )
    }
    private val categoryNotInitiated = Transformations.map(_currentTransaction) {
        it.category == "?"
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Submit Checks
     */
    ////////////////////////////////////////////////////////////////////////////////

    private enum class SubmitReadyState {
        NOT_READY_ERROR,
        NOT_READY_PENDING_OP,
        NOT_READY_CATEGORY_EMPTY,
        NOT_READY_OPERAND1_ZERO,
        NOT_READY_MEMO_EMPTY,
        READY
    }

    val memoText = MutableLiveData<String>(oldTransaction.memo)  // Exception where MutableLiveData2 fails - two-way data binding
    private val memoIsNullOrBlank = Transformations.map(memoText) {
        _currentTransaction.value = _currentTransaction.value.copy(
            memo = it.trim()
        )
        it.isNullOrBlank()
    }

    private val _submitReady = MediatorLiveData<SubmitReadyState>().apply {
        addSource(_error) { value = updateSubmitReady() }
        addSource(_pendingOperation) { value = updateSubmitReady() }
        addSource(categoryNotInitiated) { value = updateSubmitReady() }
        addSource(operand1IsZero) { value = updateSubmitReady() }
        addSource(memoIsNullOrBlank) { value = updateSubmitReady() }
    }
    private fun updateSubmitReady(): SubmitReadyState {
        return when {
            _error.value != null -> SubmitReadyState.NOT_READY_ERROR
            _pendingOperation.value != null -> SubmitReadyState.NOT_READY_PENDING_OP
            categoryNotInitiated.value ?: true -> SubmitReadyState.NOT_READY_CATEGORY_EMPTY
            operand1IsZero.value ?: true -> SubmitReadyState.NOT_READY_OPERAND1_ZERO
            memoIsNullOrBlank.value ?: true -> SubmitReadyState.NOT_READY_MEMO_EMPTY
            else -> SubmitReadyState.READY
        }
    }

    val submitBTText = Transformations.map(_submitReady) {
        when (it) {
            SubmitReadyState.NOT_READY_ERROR,
            SubmitReadyState.NOT_READY_PENDING_OP -> IconHandler.getDisplayHex("F01FC")  // EQUAL
            SubmitReadyState.NOT_READY_CATEGORY_EMPTY,
            SubmitReadyState.NOT_READY_OPERAND1_ZERO,
            SubmitReadyState.NOT_READY_MEMO_EMPTY -> IconHandler.getDisplayHex("F05E1")  // CHECK_CIRCLE_OUTLINE
            SubmitReadyState.READY -> IconHandler.getDisplayHex("F05E0")  // CHECK_CIRCLE
            else -> throw IllegalStateException("Unknown SubmitReadyState reached: $it")
        }
    }

    fun updateCurrency(currencyCode: String) {
        _currentTransaction.value = _currentTransaction.value.copy(
            originalCurrency = currencyCode
        )
    }

    fun submitBTOnClick() {
        when (_submitReady.value) {
            SubmitReadyState.NOT_READY_ERROR -> {
            }
            SubmitReadyState.NOT_READY_PENDING_OP -> operatorPressed("=")
            SubmitReadyState.NOT_READY_CATEGORY_EMPTY -> _showToastText.value =
                "Please select a category."
            SubmitReadyState.NOT_READY_OPERAND1_ZERO -> _showToastText.value =
                "Please enter an amount."
            SubmitReadyState.NOT_READY_MEMO_EMPTY -> _showToastText.value = "Please enter a memo."
            SubmitReadyState.READY -> {
                viewModelScope.launch {
                    UserRepository.getInstance().upsertTransactionSuspend(
                        _currentTransaction.value.copy(
                            originalAmount = CurrencyHandler.displayAmount(
                                BigDecimal(
                                    _currentTransaction.value.originalAmount
                                )
                            )
                        )
                    )
                    _navigateUp.value = true
                }
            }
        }
    }

    private val _showToastText = MutableLiveData2<String?>(null)
    val showToastText: LiveData<String?>
        get() = _showToastText

    fun toastShown() {
        _showToastText.value = null
    }

    private val _navigateUp = MutableLiveData2(false)
    val navigateUp: LiveData<Boolean>
        get() = _navigateUp

    fun navigateUpHandled() {
        _navigateUp.value = false
    }
}