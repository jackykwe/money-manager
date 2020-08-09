package com.kaeonx.moneymanager.userrepository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaeonx.moneymanager.userrepository.domain.Budget

@Entity(tableName = "budgets_table")
data class DatabaseBudget(
    @PrimaryKey val category: String,
    @ColumnInfo(name = "original_currency") val originalCurrency: String,
    @ColumnInfo(name = "original_amount") val originalAmount: String
)

fun List<DatabaseBudget>.toDomain(): List<Budget> = map {
    Budget(
        category = it.category,
        originalCurrency = it.originalCurrency,
        originalAmount = it.originalAmount
    )
}