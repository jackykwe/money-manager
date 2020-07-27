package com.kaeonx.moneymanager.fragments.transactions

import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.toDisplayString
import com.kaeonx.moneymanager.customclasses.toFormattedString
import com.kaeonx.moneymanager.customclasses.toIconHex
import com.kaeonx.moneymanager.userrepository.domain.Category
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

private const val TAG = "BDVM"

class TransactionsBSDFViewModel(private val oldTransaction: Transaction): ViewModel() {

    private val initCalendar: Calendar by lazy {
        val c = Calendar.getInstance()
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND))
        c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND))
        c
    }

    init {
        if (oldTransaction.transactionId == null) {
            // New transaction
            oldTransaction.apply {
                timestamp = initCalendar.timeInMillis
                type = "Expenses" // TODO: Tie to default
                category = "CATPLACEHOLDER"
                account = "ACCPLACEHOLDER"  // TODO: Tie to default
                memo = ""
                originalCurrency = "SGD"  // TODO: Tie to home currency
                originalAmount = "0"  // MAKE SURE THIS ISN'T AN EMPTY STRING. BigDecimal("") will give problems.
            }
        }
    }
    private val currentTransaction by lazy { oldTransaction.copy() }
    fun changesWereMade(): Boolean {
        return oldTransaction != currentTransaction
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

    private val _pendingOperation = MutableLiveData<String?>(null)
    val pendingOpTVText: LiveData<String?>
        get() = _pendingOperation

    private val _error = MutableLiveData<ErrorType?>(null)
    val error: LiveData<ErrorType?>
        get() = _error
    val backspaceBTText = Transformations.map(_error) {
        when (it) {
            null -> "F0B5C".toIconHex()  // BACKSPACE_OUTLINE
            else -> "F006E".toIconHex()  // BACKSPACE_BLACK
        }
    }

    private val _amountTVText = MutableLiveData<String?>(oldTransaction.originalAmount)  // Actually not nullable
    val amountTVText = MediatorLiveData<String>().apply {
        addSource(_amountTVText) { value = updateAmountTVText() }
        addSource(_error) { value = updateAmountTVText() }
    }
    private val _operand1IsZero = Transformations.map(_amountTVText) { BigDecimal(it).compareTo(BigDecimal.ZERO) == 0 }
    private fun updateAmountTVText(): String {
        return when (_error.value) {
            null -> {
                currentTransaction.originalAmount = BigDecimal(_amountTVText.value!!).toDisplayString()
                _amountTVText.value!!
            }
            ErrorType.OVERFLOW -> "e: overflow"
            ErrorType.DIV_ZERO -> "e: div zero"
        }
    }

    // the only other possible values are valid values of _amountTVText
    private val _pendingAmtTVText = MutableLiveData<String?>(null)
    val pendingAmtTVText: LiveData<String?>
        get() = _pendingAmtTVText


    // Used when adding characters to the MLD string.
    private fun typeInto(mld: MutableLiveData<String?>, newChar: String) {
        val oldValue = mld.value
        if (oldValue.isNullOrEmpty()) {
            // Initialise value for _pendingAmtTVText
            mld.value = if (newChar == ".") "0." else newChar
        } else if (mld.value == "0" && newChar != ".") {
            // Replaces 0 with 1~9
            mld.value = newChar
        } else if (oldValue.contains(".")) {
            // If decimal
            if ((oldValue.substring(oldValue.indexOf(".") + 1).length < MAX_DP) && newChar != ".") {
                // If less than 2 dp && newChar is not .
                mld.value = oldValue + newChar
            }
        } else {
            // If not decimal
            if (oldValue.length != MAX_INT || newChar == ".") {
                // Not allowed to add if length == 6 && newChar != "."
                mld.value = oldValue + newChar
            }
        }
    }

    fun digitDecimalPressed(digitDecimal: String) {
        if (_pendingOperation.value == null) {
            typeInto(_amountTVText, digitDecimal)
        } else {
            typeInto(_pendingAmtTVText, digitDecimal)
        }
    }

    // Arithmetic handled in here
    fun operatorPressed(operator: String) {
        var operand1 = BigDecimal(_amountTVText.value!!)  // _operand1 will never be null
        if (!_pendingAmtTVText.value.isNullOrEmpty()) {
            // Perform arithmetic
            val operand2 = BigDecimal(_pendingAmtTVText.value!!)  // operand2 will not be null/empty by the time the code reaches here
            when (_pendingOperation.value) {
                "+" -> operand1 = operand1.plus(operand2)
                "-" -> operand1 = operand1.minus(operand2)
                "×" -> operand1 = operand1.times(operand2)
                "÷" -> {
                    if (operand2.compareTo(BigDecimal.ZERO) == 0) {
                        _error.value = ErrorType.DIV_ZERO
                        _pendingOperation.value = null
                        return
                    } else {
                        operand1 = operand1.divide(operand2, MathContext(9, RoundingMode.HALF_UP))  // div operator uses RoundingMode.HALF_EVEN
                    }
                }
                null -> throw IllegalStateException("_pendingAmtTVText is not null or empty, but _pendingOperation is null.")
                else -> throw IllegalArgumentException("Illegal pendingOperation: ${_pendingOperation.value}")
            }
            _pendingAmtTVText.value = null
        }

        // Must be done after arithmetic, as the old pendingOperationMLD.value is used in the arithmetic.
        _pendingOperation.value = if (operator == "=") null else operator

        // Check if there's overflow after arithmetic. If not, check if operand1 is zero
        if (operand1 >= BigDecimal("1E$MAX_INT")) {
            _error.value = ErrorType.OVERFLOW
            _pendingOperation.value = null
        } else {
            // If the code reaches here, operand1 has successfully been updated to a new value
            // Added a check to prevent negative values
            _amountTVText.value = if (operand1 < BigDecimal.ZERO) {
                _showToastText.value = "Negative values are not allowed"
                "0"
            } else operand1.toDisplayString()
        }
    }

    fun backspacePressed() {
        if (_error.value != null) {
            // There is an error
            backspaceLongPressed()
        } else {
            // There is no error
            if (_pendingOperation.value == null) {
                // Handle _amountTVText
                val text = _amountTVText.value!!
                _amountTVText.value = if (text.length == 1) "0" else text.substring(0, text.length - 1)
            } else {
                // Handle _pendingAmtTVText
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
        _pendingOperation.value = null
        _error.value = null
        _amountTVText.value = "0"
        _pendingAmtTVText.value = null
//        submitReadyMLD.value = SubmitReadyState.NOT_READY_OPERAND1_ZERO
        return true
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Date & Time Manipulation
     */
    ////////////////////////////////////////////////////////////////////////////////

    private val _calendar = MutableLiveData<Calendar>(initCalendar)
    val calendar: LiveData<Calendar>
        get() = _calendar
    val dateTimeBTText = Transformations.map(_calendar) {
        currentTransaction.timestamp = it.timeInMillis
        val time = it.toFormattedString("HHmm")
        val date = it.toFormattedString("ddMMyy")
        buildSpannedString {
            bold {
                append(time)
            }
            append("\n$date")
        }
    }

    fun updateCalendar(calendar: Calendar) {
        _calendar.value = calendar
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Account and Category Manipulation
     */
    ////////////////////////////////////////////////////////////////////////////////
    fun updateCategory(category: Category) {
        currentTransaction.category = category.name
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

    private val _categoryIsNull = MutableLiveData(false)

    val memoText = MutableLiveData<String>("")
    private val _memoIsNullOrBlank = Transformations.map(memoText) {
        currentTransaction.memo = it
        it.isNullOrBlank()
    }

    private val _submitReady = MediatorLiveData<SubmitReadyState>().apply {
        addSource(_error) { value = updateSubmitReady() }
        addSource(_pendingOperation) { value = updateSubmitReady() }
        addSource(_categoryIsNull) { value = updateSubmitReady() }
        addSource(_operand1IsZero) { value = updateSubmitReady() }
        addSource(_memoIsNullOrBlank) { value = updateSubmitReady() }
    }
    private fun updateSubmitReady(): SubmitReadyState {
        return when {
            _error.value != null -> SubmitReadyState.NOT_READY_ERROR
            _pendingOperation.value != null -> SubmitReadyState.NOT_READY_PENDING_OP
            _categoryIsNull.value!! -> SubmitReadyState.NOT_READY_CATEGORY_EMPTY
            _operand1IsZero.value ?: true -> SubmitReadyState.NOT_READY_OPERAND1_ZERO
            _memoIsNullOrBlank.value ?: true -> SubmitReadyState.NOT_READY_MEMO_EMPTY
            else -> SubmitReadyState.READY
        }
    }

    val submitBTText = Transformations.map(_submitReady) {
        when (it) {
            SubmitReadyState.NOT_READY_ERROR,
            SubmitReadyState.NOT_READY_PENDING_OP -> "F01FC".toIconHex()  // EQUAL
            SubmitReadyState.NOT_READY_CATEGORY_EMPTY,
            SubmitReadyState.NOT_READY_OPERAND1_ZERO,
            SubmitReadyState.NOT_READY_MEMO_EMPTY -> "F05E1".toIconHex()  // CHECK_CIRCLE_OUTLINE
            SubmitReadyState.READY -> "F05E0".toIconHex()  // CHECK_CIRCLE
            else -> throw IllegalStateException("Unknown SubmitReadyState reached: $it")
        }
    }

    fun submitBTOnClick() {
        when (val state = _submitReady.value) {
            SubmitReadyState.NOT_READY_ERROR -> { }
            SubmitReadyState.NOT_READY_PENDING_OP -> operatorPressed("=")
            SubmitReadyState.NOT_READY_CATEGORY_EMPTY -> _showToastText.value = "Please select a category."
            SubmitReadyState.NOT_READY_OPERAND1_ZERO -> _showToastText.value = "Please enter an amount."
            SubmitReadyState.NOT_READY_MEMO_EMPTY -> _showToastText.value = "Please enter a memo."
            SubmitReadyState.READY -> { _submitTrigger.value = currentTransaction }
            else -> throw IllegalStateException("Unknown SubmitReadyState reached: $state")
        }
    }
    private val _showToastText = MutableLiveData<String?>(null)
    val showToastText: LiveData<String?>
        get() = _showToastText
    fun toastShown() {
        _showToastText.value = null
    }
    private val _submitTrigger = MutableLiveData<Transaction?>(null)
    val submitTrigger: LiveData<Transaction?>
        get() = _submitTrigger
    fun submitHandled() {
        _submitTrigger.value = null
    }
}