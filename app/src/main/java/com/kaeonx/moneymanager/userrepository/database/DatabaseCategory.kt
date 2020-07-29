package com.kaeonx.moneymanager.userrepository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaeonx.moneymanager.userrepository.domain.Category

@Entity(tableName = "categories_table")
data class DatabaseCategory(
    @PrimaryKey(autoGenerate = true) val categoryId: Int,
    val type: String,
    val name: String,
    @ColumnInfo(name = "icon_hex") val iconHex: String,
    @ColumnInfo(name = "colour_string") val colourString: String
)

fun List<DatabaseCategory>.toDomain(): List<Category> = map {
    Category(
        categoryId = it.categoryId,
        type = it.type,
        name = it.name,
        iconHex = it.iconHex,
        colourString = it.colourString
    )
}