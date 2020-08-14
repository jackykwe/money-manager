package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.DatabaseTransaction
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@JsonClass(generateAdapter = true)
@Parcelize
data class Transaction(
    @Json(name = "i") val transactionId: Int?,
    @Json(name = "t") var timestamp: Long,
    @Json(name = "y") var type: String,
    @Json(name = "c") var category: String,
    @Json(name = "a") var account: String,
    @Json(name = "m") var memo: String,
    @Json(name = "x") var originalCurrency: String,
    @Json(name = "v") var originalAmount: String
) : Parcelable {

    @IgnoredOnParcel
    @Transient
    internal lateinit var homeAmount: BigDecimal

    fun toDatabase(): DatabaseTransaction {
        return DatabaseTransaction(
            transactionId = this.transactionId ?: 0,
            timestamp = this.timestamp,
            type = this.type,
            category = this.category,
            account = this.account,
            memo = this.memo,
            originalCurrency = this.originalCurrency,
            originalAmount = this.originalAmount
        )
    }

    fun toIconDetail(): IconDetail {
        val repository = UserRepository.getInstance()
        val categoryObj =
            repository.categories.value!!.find { it.name == this.category && it.type == this.type }
                ?: Category(null, type, category, "F02D6", "Black")
        val accountObj = repository.accounts.value!!.find { it.name == this.account }
            ?: Account(null, account, "TRANSPARENT")
        return IconDetail(categoryObj.iconHex, categoryObj.colourFamily, accountObj.colourFamily)
    }

    fun importEnsureValid() {
        fun errorText(text: String, field: Any? = null, additionalHint: String? = null): String {
            return when {
                additionalHint == null && field == null -> "error at Transaction id: \"$transactionId\"\n$text"
                additionalHint == null -> "error at Transaction id: \"$transactionId\"\n$text: \"$field\""
                field == null -> "error at Transaction id: \"$transactionId\"\n$text\n$additionalHint"
                else -> "error at Transaction id: \"$transactionId\"\n$text: \"$field\"\n$additionalHint"
            }
        }
        if (transactionId == null) throw IllegalStateException("found Transaction with missing id")
        if (transactionId <= 0) throw IllegalStateException(
            errorText(
                "non-positive id",
                transactionId
            )
        )
        if (timestamp < 0) throw IllegalStateException(errorText("negative timestamp", timestamp))
        if (type !in listOf(
                "Income",
                "Expenses"
            )
        ) throw IllegalStateException(
            errorText(
                "invalid type",
                type,
                "Type should be either \"Income\" or \"Expenses\"."
            )
        )
        if (category in listOf("Overall", "Add…", "(multiple)")) throw IllegalStateException(
            errorText("reserved category", category)
        )
        if (category.trim().isBlank()) throw IllegalStateException(errorText("blank category"))
        if (category.trim() != category) throw IllegalStateException(
            errorText(
                "category has extra whitespace",
                category
            )
        )
        if (account == "Add…") throw IllegalStateException(errorText("reserved account", account))
        if (account.trim().isBlank()) throw IllegalStateException(errorText("blank account"))
        if (account.trim() != account) throw IllegalStateException(
            errorText(
                "account has extra whitespace",
                account
            )
        )
        if (memo.trim().isBlank()) throw IllegalStateException(errorText("blank memo"))
        if (memo.trim() != memo) throw IllegalStateException(
            errorText(
                "memo has extra whitespace",
                memo
            )
        )
        if (originalCurrency !in App.context.resources.getStringArray(R.array.ccc_currencies_values)) throw IllegalStateException(
            errorText("invalid currency", originalCurrency)
        )
        if (originalAmount.isBlank()) throw IllegalStateException(
            errorText(
                "blank amount",
                originalAmount
            )
        )
        try {
            BigDecimal(originalAmount)
        } catch (e: Exception) {
            throw IllegalStateException(
                errorText(
                    "malformed amount",
                    originalAmount,
                    "Check for any invalid characters."
                )
            )
        }
        if (BigDecimal(originalAmount) <= BigDecimal.ZERO) throw IllegalStateException(
            errorText(
                "non-positive amount",
                originalAmount
            )
        )
        if (BigDecimal(originalAmount) > BigDecimal("999999.99")) throw IllegalStateException(
            errorText("amount exceeded cap", originalAmount, "Max value is 999999.99.")
        )
        if (originalAmount != CurrencyHandler.displayAmount(BigDecimal(originalAmount))) throw IllegalStateException(
            errorText(
                "bad amount format",
                originalAmount,
                "Number of decimal places should be 0 or 2."
            )
        )
    }
}

internal suspend fun List<Transaction>.toDayTransactions(): List<DayTransactions> {
    // List<Transaction> is sorted in DESCENDING timestamp order.
    if (isEmpty()) return listOf()
    fun getYMDIdentifier(timestamp: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.YEAR) * 10000 + cal.get(Calendar.MONTH) * 100 + cal.get(Calendar.DAY_OF_MONTH)
    }
    return withContext(Dispatchers.Default) {
        val grouping = mutableMapOf<Int, DayTransactions>()
        forEach {
            ensureActive()
            val identifier = getYMDIdentifier(it.timestamp)
            grouping[identifier] = (grouping[identifier] ?: DayTransactions(
                ymdIdentifier = identifier,
                ymdCalendar = CalendarHandler.getCalendar(it.timestamp),  // This calendar only needs to be accurate to the day. Hours/minutes/etc. don't matter.
                dayIncome = null,
                dayExpenses = null,
                incomeAllHome = false,
                expensesAllHome = false,
                transactions = ArrayList()
            )).apply {
                transactions.add(it)
            }
        }

        // Calculates currency, dayIncome and dayExpenses.
        return@withContext grouping.values.map {
            ensureActive()
            it.apply {
                // Check if all transactions within each day have the same currency, then calculate new income & expense
                incomeAllHome = transactions.typeAllHomeCurrency("Income")
                expensesAllHome = transactions.typeAllHomeCurrency("Expenses")

                // Note: In all calculations, each individual transaction is converted to Home Currency first (if needed)
                val (income, expenses) = transactions.calculateIncomeExpenses()
                dayIncome = income
                dayExpenses = expenses
            }
        }
    }
}

// Helper function for List<Transaction>.toDayTransactions()
private fun List<Transaction>.typeAllHomeCurrency(type: String): Boolean {
    return filter { it.type == type }
        .all { it.originalCurrency == UserPDS.getString("ccc_home_currency") }
}

// Helper function for List<Transaction>.toDayTransactions()
private fun List<Transaction>.calculateIncomeExpenses(): IncomeExpenses {
    var gotIncome = false
    var gotExpenses = false
    var income = BigDecimal.ZERO
    var expenses = BigDecimal.ZERO
    forEach {
        when (it.type) {
            "Income" -> {
                gotIncome = true
                income = if (it.originalCurrency == UserPDS.getString("ccc_home_currency")) {
                    income.plus(BigDecimal(it.originalAmount))
                } else {
                    val value = CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        UserPDS.getString("ccc_home_currency")
                    )
                    income.plus(value)
                }
            }
            "Expenses" -> {
                gotExpenses = true
                expenses = if (it.originalCurrency == UserPDS.getString("ccc_home_currency")) {
                    expenses.plus(BigDecimal(it.originalAmount))
                } else {
                    val value = CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        UserPDS.getString("ccc_home_currency")
                    )
                    expenses.plus(value)
                }
            }
            else -> throw IllegalArgumentException("Unknown type given: ${it.type}")
        }
    }
    val finalIncome = if (gotIncome) CurrencyHandler.displayAmount(income) else null
    val finalExpenses = if (gotExpenses) CurrencyHandler.displayAmount(expenses) else null
    return IncomeExpenses(finalIncome, finalExpenses)
}

internal fun List<Transaction>.toDatabase(): List<DatabaseTransaction> = map {
    DatabaseTransaction(
        transactionId = it.transactionId!!,
        timestamp = it.timestamp,
        type = it.type,
        category = it.category,
        account = it.account,
        memo = it.memo,
        originalCurrency = it.originalCurrency,
        originalAmount = it.originalAmount
    )
}