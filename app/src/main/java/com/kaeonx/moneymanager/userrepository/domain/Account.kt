package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.database.DatabaseAccount
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

internal const val ACCOUNT_NAME_MAX_LENGTH = 100

@JsonClass(generateAdapter = true)
@Parcelize
data class Account(
    @Json(name = "i") val accountId: Int?,
    @Json(name = "n") var name: String,
    @Json(name = "c") var colourFamily: String
) : Parcelable {

    fun toIconDetail(): IconDetail = IconDetail(
        iconHex = "F02D6",
        iconBGColourFamily = "TRANSPARENT",
        iconRingColourFamily = this.colourFamily
    )

    fun toDatabase(): DatabaseAccount = DatabaseAccount(
        accountId = this.accountId ?: 0,
        name = this.name,
        colourString = this.colourFamily
    )

    fun importEnsureValid() {
        fun errorText(text: String, field: Any? = null, additionalHint: String? = null): String {
            return when {
                additionalHint == null && field == null -> "error at Account id: \"$accountId\"\n$text"
                additionalHint == null -> "error at Account id: \"$accountId\"\n$text: \"$field\""
                field == null -> "error at Account id: \"$accountId\"\n$text\n$additionalHint"
                else -> "error at Account id: \"$accountId\"\n$text: \"$field\"\n$additionalHint"
            }
        }
        if (accountId == null) throw IllegalStateException("found Account with missing id")
        if (accountId <= 0) throw IllegalStateException(errorText("non-positive id", accountId))
        if (name == "Add…") throw IllegalStateException(errorText("reserved name", name))
        if (name.trim().isBlank()) throw IllegalStateException(errorText("blank name"))
        if (name.trim().length > ACCOUNT_NAME_MAX_LENGTH) throw IllegalStateException(
            errorText("name is too long")
        )
        if (name.trim() != name) throw IllegalStateException(
            errorText(
                "name has extra whitespace",
                name
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

internal fun List<Account>.toDatabase(): List<DatabaseAccount> = map {
    DatabaseAccount(
        accountId = it.accountId!!,
        name = it.name,
        colourString = it.colourFamily
    )
}
