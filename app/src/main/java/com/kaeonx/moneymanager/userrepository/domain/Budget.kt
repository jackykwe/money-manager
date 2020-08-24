package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.DatabaseBudget
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

internal const val BUDGET_ORIGAMT_MAX_LENGTH = 10

@JsonClass(generateAdapter = true)
@Parcelize
data class Budget(
    @Json(name = "c") val category: String,
    @Json(name = "x") val originalCurrency: String,
    @Json(name = "v") val originalAmount: String
) : Parcelable {

    fun toIconDetail(): IconDetail {
        if (category == "Overall") return IconDetail("F04A0", "Black", "TRANSPARENT")
        val categoryObj = UserRepository.getInstance().categories.value!!
            .find { it.name == this.category && it.type == "Expenses" }
//            ?: throw IllegalStateException("Tried to edit budget for category that does not exist.")
            ?: Category(null, "Expenses", category, "F02D6", "Black")
        return IconDetail(
            iconHex = categoryObj.iconHex,
            iconBGColourFamily = categoryObj.colourFamily,
            iconRingColourFamily = "TRANSPARENT"
        )
    }

    fun toDatabase(): DatabaseBudget = DatabaseBudget(
        category = category,
        originalCurrency = originalCurrency,
        originalAmount = originalAmount
    )

    fun importEnsureValid() {
        fun truncate(string: String): String {
            val shortened = string.take(15)
            return if (shortened != string) "$shortened…" else string
        }

        fun errorText(text: String, field: Any? = null, additionalHint: String? = null): String {
            return when {
                additionalHint == null && field == null -> "error at Budget for category: \"${
                    truncate(
                        category
                    )
                }\"\n$text"
                additionalHint == null -> "error at Budget for category: \"${truncate(category)}\"\n$text: \"$field\""
                field == null -> "error at Budget for category: \"${truncate(category)}\"\n$text\n$additionalHint"
                else -> "error at Budget for category: \"${truncate(category)}\"\n$text: \"$field\"\n$additionalHint"
            }
        }
        if (category in listOf(
                "Add…",
                "(multiple)"
            )
        ) throw IllegalStateException(errorText("reserved category", category))
        if (category.trim().isBlank()) throw IllegalStateException(errorText("blank category"))
        if (category.trim() != category) throw IllegalStateException(
            errorText(
                "category has extra whitespace",
                category
            )
        )
        if (originalCurrency !in App.context.resources.getStringArray(R.array.ccc_home_currency_values)) throw IllegalStateException(
            errorText("invalid currency", originalCurrency)
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
        if (originalAmount.isBlank()) throw IllegalStateException(
            errorText(
                "blank amount",
                originalAmount
            )
        )
        if (originalAmount.trim().length > BUDGET_ORIGAMT_MAX_LENGTH) throw IllegalStateException(
            errorText("amount is too long")
        )
        if (BigDecimal(originalAmount) <= BigDecimal.ZERO) throw IllegalStateException(
            errorText(
                "non-positive amount",
                originalAmount
            )
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

internal fun List<Budget>.toDatabase(): List<DatabaseBudget> = map {
    DatabaseBudget(
        category = it.category,
        originalCurrency = it.originalCurrency,
        originalAmount = it.originalAmount
    )
}