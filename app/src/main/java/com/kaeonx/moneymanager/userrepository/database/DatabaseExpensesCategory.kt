package com.kaeonx.moneymanager.userrepository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaeonx.moneymanager.userrepository.domain.Category

@Entity(tableName = "expenses_categories_table")
data class DatabaseExpensesCategory(
    val type: String,
    @PrimaryKey val name: String,
    @ColumnInfo(name = "icon_hex") val iconHex: String,
    @ColumnInfo(name = "colour_string") val colourString: String
)

fun List<DatabaseExpensesCategory>.toDomain(): List<Category> = map {
    Category(
        type = it.type,
        name = it.name,
        iconHex = it.iconHex,
        colourString = it.colourString
    )
}