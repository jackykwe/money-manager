package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.handlers.IconHandler.Companion.MAX_SUPPORTED_HEX
import com.kaeonx.moneymanager.userrepository.database.DatabaseCategory
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

internal const val CATEGORY_NAME_MAX_LENGTH = 100

@JsonClass(generateAdapter = true)
@Parcelize
data class Category(
    @Json(name = "i") val categoryId: Int?,
    @Json(name = "y") val type: String,
    @Json(name = "n") var name: String,
    @Json(name = "h") var iconHex: String,
    @Json(name = "c") var colourFamily: String
) : Parcelable {

    fun toIconDetail(): IconDetail = IconDetail(
        iconHex = this.iconHex,
        iconBGColourFamily = this.colourFamily,
        iconRingColourFamily = "TRANSPARENT"
    )

    fun toDatabase(): DatabaseCategory = DatabaseCategory(
        categoryId = this.categoryId ?: 0,
        type = this.type,
        name = this.name,
        iconHex = this.iconHex,
        colourString = this.colourFamily
    )

    fun importEnsureValid() {
        fun errorText(text: String, field: Any? = null, additionalHint: String? = null): String {
            return when {
                additionalHint == null && field == null -> "error at Category id: \"$categoryId\"\n$text"
                additionalHint == null -> "error at Category id: \"$categoryId\"\n$text: \"$field\""
                field == null -> "error at Category id: \"$categoryId\"\n$text\n$additionalHint"
                else -> "error at Category id: \"$categoryId\"\n$text: \"$field\"\n$additionalHint"
            }
        }
        if (categoryId == null) throw IllegalStateException("found Category with missing id")
        if (categoryId <= 0) throw IllegalStateException(errorText("non-positive id", categoryId))
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
        if (name in listOf(
                "Overall",
                "Add…",
                "(multiple)"
            )
        ) throw IllegalStateException(errorText("reserved name", name))
        if (name.trim().isBlank()) throw IllegalStateException(errorText("blank name"))
        if (name.trim().length > CATEGORY_NAME_MAX_LENGTH) throw IllegalStateException(
            errorText("name is too long")
        )
        if (name.trim() != name) throw IllegalStateException(
            errorText(
                "name has extra whitespace",
                name
            )
        )
        if (!IconHandler.iconHexIsValid(iconHex)) throw IllegalStateException(
            errorText(
                "invalid Icon ID",
                iconHex,
                "Valid hex values from F0001 to $MAX_SUPPORTED_HEX"
            )
        )
        try {
            if (colourFamily in listOf("Black", "TRANSPARENT")) throw Exception()
            ColourHandler.getColourObjectOf(colourFamily)
        } catch (e: Exception) {
            throw IllegalStateException(
                errorText(
                    "invalid colour family",
                    colourFamily,
                    "Refer to examples from exported data."
                )
            )
        }
    }
}

internal fun List<Category>.toDatabase(): List<DatabaseCategory> = map {
    DatabaseCategory(
        categoryId = it.categoryId!!,
        type = it.type,
        name = it.name,
        iconHex = it.iconHex,
        colourString = it.colourFamily
    )
}