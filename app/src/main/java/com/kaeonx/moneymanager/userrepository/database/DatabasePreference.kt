package com.kaeonx.moneymanager.userrepository.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences_table")
data class DatabasePreference(
    @PrimaryKey val key: String,
    val valueInteger: Int?,
    val valueText: String?
)

//fun List<DatabasePreference>.toDomain(): List<Preference> = map {
//    Preference(
//        key = it.key,
//        valueInteger = it.valueInteger,
//        valueReal = it.valueReal,
//        valueText = it.valueText
//    )
//}

fun List<DatabasePreference>.toMap(): Map<String, Any> {
    val result = mutableMapOf<String, Any>()
    forEach {
        result[it.key] = it.valueText ?: it.valueInteger
                ?: throw IllegalStateException("Error accessing preference key ${it.key}: has no value")
    }
    return result
}