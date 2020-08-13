package com.kaeonx.moneymanager.userrepository.domain

import com.kaeonx.moneymanager.userrepository.database.DatabasePreference
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Preference(
    @Json(name = "k") val key: String,
    @Json(name = "i") val valueInteger: Int?,
    @Json(name = "t") val valueText: String?
) : Serializable {

    fun toDatabase(): DatabasePreference = DatabasePreference(
        key = this.key,
        valueInteger = this.valueInteger,
        valueText = this.valueText
    )

}

internal fun List<Preference>.toDatabase(): List<DatabasePreference> = map {
    DatabasePreference(
        key = it.key,
        valueInteger = it.valueInteger,
        valueText = it.valueText
    )
}