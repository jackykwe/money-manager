package com.kaeonx.moneymanager.fragments.transactions

import android.app.Application
import android.text.Editable
import android.text.Html
import android.util.Log
import androidx.lifecycle.*
import java.math.BigDecimal

private const val TAG = "BDVM"

class TransactionsBSDFViewModel(application: Application): AndroidViewModel(application) {

    enum class ErrorType {
        OVERFLOW,
        DIV_ZERO
    }

    private enum class SubmitReadyState {
        NOT_READY_ERROR,
        NOT_READY_PENDING_OP,
        NOT_READY_CATEGORY_EMPTY,
        NOT_READY_OPERAND1_ZERO,
        NOT_READY_MEMO_EMPTY,
        READY
    }

    companion object {
        internal const val MAX_DP = 2 // Maximum number of digits after decimal point
        internal const val MAX_INT = 6 // Maximum number of digits before decimal point
    }

    fun hexToIcon(id: String): String {
        // id looks like Fxxxx
        val internalId = id.substring(0 until 5)
        return Html.fromHtml("&#x$internalId;", Html.FROM_HTML_MODE_LEGACY).toString()
    }



    private var operand1MLD = MutableLiveData("0")
    private var operand1IsZeroMLD = MutableLiveData(true)

    private var operand2MLD = MutableLiveData<String>()
    val pendingAmtTVText: LiveData<String?>
        get() = operand2MLD

    private var pendingOperationMLD = MutableLiveData<String>(null)
    val pendingOpTVText: LiveData<String?>
        get() = pendingOperationMLD

    private var errorMLD = MutableLiveData<ErrorType?>(null)
    val errorLD: LiveData<ErrorType?>
        get() = errorMLD
    val backspaceBTText = Transformations.map(errorMLD) {
        when (it) {
            null -> hexToIcon("F0B5C")  // BACKSPACE_OUTLINE
            else -> hexToIcon("F006E")  // BACKSPACE_BLACK
        }
    }


    val amountTVTextLD = MediatorLiveData<String>().apply {
        addSource(operand1MLD) {
//            if (errorMLD.value != null)
            value = it
        }
        addSource(errorMLD) {
            value = when (it) {
                null -> operand1MLD.value
                ErrorType.OVERFLOW -> "e: overflow"
                ErrorType.DIV_ZERO -> "e: div zero"
            }
        }
    }


    internal fun declareOperand1(value: String) {
        operand1MLD.value = value
        updateOperand1IsZero()
    }

    private fun updateOperand1IsZero() {
        operand1IsZeroMLD.value = operand1MLD.value.toString().toBigDecimal().compareTo(BigDecimal.ZERO) == 0
        updateSubmitReadyMLD()
    }

    // Used when adding characters to the MLD string.
    private fun typeIntoMLDGate(mld: MutableLiveData<String>, newChar: String) {
        if (mld.value.isNullOrEmpty()) {
            if (newChar == ".") {
                mld.value = "0."
            } else {
                mld.value = newChar
            }
        } else if (mld.value == "0" && newChar != ".") {
            mld.value = newChar
        } else if (mld.value!!.contains(".")) { // If decimal
            if ((mld.value!!.substring(mld.value!!.indexOf(".") + 1).length < MAX_DP) && newChar != ".") {
                // If less than 2 dp && newChar is not .
                mld.value = mld.value + newChar
            }
        } else { // If not decimal
            if (mld.value!!.length != MAX_INT || newChar == ".") {
                // Not allowed to add if length == 6 & newChar != "."
                mld.value = mld.value + newChar
            }
        }
        updateOperand1IsZero()
    }

    fun digitDecimalPressed(digitDecimal: String) {
        Log.d(TAG, "digitDecimalPressed: $digitDecimal")
//        if (pendingOperationMLD.value == null) {
//            typeIntoMLDGate(operand1MLD, digitDecimal)
//        } else {
//            typeIntoMLDGate(operand2MLD, digitDecimal)
//        }
    }

    // Arithmetic handled in here
    fun operatorPressed(operator: String) {
        Log.d(TAG, "operator $operator pressed")
//        var operand1 = operand1MLD.value!!.toBigDecimal() // operand1 will never be null
//        if (!operand2MLD.value.isNullOrEmpty()) {
//            // Perform arithmetic
//            val operand2 = operand2MLD.value!!.toBigDecimal() // operand2 will not be null/empty by the time the code reaches here
//            when (pendingOperationMLD.value) {
//                "+" -> operand1 = operand1.plus(operand2)
//                "-" -> operand1 = operand1.minus(operand2)
//                "×" -> operand1 = operand1.times(operand2)
//                "÷" -> {
//                    if (operand2.compareTo(BigDecimal.ZERO) == 0) {
//                        errorMLD.value = ErrorType.DIV_ZERO
//                        updateSubmitReadyMLD()
//                        return
//                    } else {
//                        operand1 = operand1.divide(operand2, MathContext(9, RoundingMode.HALF_UP)) // div operator uses RoundingMode.HALF_EVEN
//                    }
//                }
//                null -> { }
//                else -> throw IllegalArgumentException("Illegal pendingOperation: ${pendingOperationMLD.value}")
//            }
//            Log.d(TAG, "operatorPressed: operand1: $operand1")
//            operand1MLD.value = CurrencyHandler.bigDecimalDisplay(operand1)
//            Log.d(TAG, "operatorPressed: operand1MLD.value: ${operand1MLD.value}")
//            operand2MLD.value = null
//        }
//
//        // Must be done after arithmetic, as the old pendingOperationMLD.value is used in the arithmetic.
//        if (operator == "=") { // operator is "="
//            pendingOperationMLD.value = null
//        } else {
//            pendingOperationMLD.value = operator
//        }
//
//        // Check if there's overflow after arithmetic. If not, check if operand1 is zero
//        if (operand1 >= BigDecimal("1E$MAX_INT")) {
//            errorMLD.value =
//                ErrorType.OVERFLOW
//            pendingOperationMLD.value = null
//            updateSubmitReadyMLD()
//        } else { // If the code reaches here, operand1 has successfully been updated to a new value
//            updateOperand1IsZero()
//        }
    }

    fun backspacePressed() {
        Log.d(TAG, "backspacePressed.")
//        if (errorMLD.value != null) {  // There is an error
//            backspaceLongPressed()
//        } else {  // There is no error
//            if (pendingOperationMLD.value == null) {
//                if (operand1MLD.value!!.length == 1) {
//                    operand1MLD.value = "0"
//                } else {
//                    operand1MLD.value = operand1MLD.value!!.substring(0, operand1MLD.value!!.length - 1)
//                }
//            } else {
//                when {
//                    operand2MLD.value.isNullOrEmpty() -> {
//                        pendingOperationMLD.value = null
//                    }
//                    else -> {
//                        operand2MLD.value = operand2MLD.value!!.substring(0, operand2MLD.value!!.length - 1)
//                    }
//                }
//            }
//            updateOperand1IsZero()
//        }
    }

    internal fun backspaceLongPressed(): Boolean {
        Log.d(TAG, "backspaceLongPressed.")
//        // Reset everything (EXCEPT memo and category) to initial state
//        operand1MLD.value = "0"
//        operand1IsZeroMLD.value = true
//        operand2MLD.value = null
//        pendingOperationMLD.value = null
//        errorMLD.value = null
//        submitReadyMLD.value = SubmitReadyState.NOT_READY_OPERAND1_ZERO
        return true
    }


    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Extensions: Strictly speaking not relevant to arithmetic operations
     */
    ////////////////////////////////////////////////////////////////////////////////
    private var categoriesIsNullMLD = MutableLiveData(true)
    private var memoIsNullOrBlankMLD = MutableLiveData(true)
    private var submitReadyMLD = MutableLiveData(SubmitReadyState.NOT_READY_CATEGORY_EMPTY)
    private var showToastTextMLD = MutableLiveData<String?>(null)
    val showToastTextLD: LiveData<String?>
        get() = showToastTextMLD


    val submitBTText = Transformations.map(submitReadyMLD) {
        when (it) {
            SubmitReadyState.NOT_READY_ERROR,
            SubmitReadyState.NOT_READY_PENDING_OP -> hexToIcon("F01FC")  // EQUAL
            SubmitReadyState.NOT_READY_CATEGORY_EMPTY,
            SubmitReadyState.NOT_READY_OPERAND1_ZERO,
            SubmitReadyState.NOT_READY_MEMO_EMPTY -> hexToIcon("F05E1")  // CHECK_CIRCLE_OUTLINE
            SubmitReadyState.READY -> hexToIcon("F05E0")  // CHECK_CIRCLE
            else -> throw IllegalStateException("Unknown SubmitReadyState reached: $it")
        }
    }

    fun submitBTOnClick() {
        when (val state = submitReadyMLD.value) {
            SubmitReadyState.NOT_READY_ERROR -> { }
            SubmitReadyState.NOT_READY_PENDING_OP -> operatorPressed("=")
            SubmitReadyState.NOT_READY_CATEGORY_EMPTY -> showToastTextMLD.value = "Please select a category."
            SubmitReadyState.NOT_READY_OPERAND1_ZERO -> showToastTextMLD.value = "Please enter an amount."
            SubmitReadyState.NOT_READY_MEMO_EMPTY -> showToastTextMLD.value = "Please enter a memo."
            SubmitReadyState.READY -> { } // submitTransactionAndDismiss()
            else -> throw IllegalStateException("Unknown SubmitReadyState reached: $state")
        }
    }

//    fun toastShown() {
//        showToastTextMLD.value = null
//    }

    internal fun updateMemoIsNullOrBlankMLD(memoEditable: Editable?) {
//        memoIsNullOrBlankMLD.value = memoEditable.isNullOrBlank()
//        updateSubmitReadyMLD()
    }

    internal fun updateCategoriesAreNullMLD() {
//        categoriesIsNullMLD.value = false
//        updateSubmitReadyMLD()
    }

    internal fun declareExistingTransaction() {
//        errorMLD.value = null
//        pendingOperationMLD.value = null
//        categoriesIsNullMLD.value = false
//        operand1IsZeroMLD.value = false
//        memoIsNullOrBlankMLD.value = false
//        updateSubmitReadyMLD()
    }

    private fun updateSubmitReadyMLD() {
//        submitReadyMLD.value = when {
//            errorMLD.value != null -> SubmitReadyState.NOT_READY_ERROR
//            pendingOperationMLD.value != null -> SubmitReadyState.NOT_READY_PENDING_OP
//            categoriesIsNullMLD.value!! -> SubmitReadyState.NOT_READY_CATEGORY_EMPTY
//            operand1IsZeroMLD.value!! -> SubmitReadyState.NOT_READY_OPERAND1_ZERO
//            memoIsNullOrBlankMLD.value!! -> SubmitReadyState.NOT_READY_MEMO_EMPTY
//            else -> SubmitReadyState.READY
//        }
    }
}