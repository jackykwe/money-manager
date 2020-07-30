package com.kaeonx.moneymanager.userrepository.domain

import com.kaeonx.moneymanager.userrepository.database.DatabasePreference
import java.io.Serializable

data class Preference(
    val key: String,
    val valueInteger: Int?,
    val valueText: String?
) : Serializable {

    fun toDatabase(): DatabasePreference = DatabasePreference(
        key = this.key,
        valueInteger = this.valueInteger,
        valueText = this.valueText
    )

}