package com.kaeonx.moneymanager.userrepository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaeonx.moneymanager.userrepository.domain.Transaction

@Entity(tableName = "transactions_table")
data class DatabaseTransaction(
    @PrimaryKey(autoGenerate = true) val transactionId: Int,
    val timestamp: Long,
    val type: String,
    val category: String,
    val account: String,
    val memo: String,
    @ColumnInfo(name = "original_currency") val originalCurrency: String,
    @ColumnInfo(name = "original_amount") val originalAmount: String
) {

    fun toDomain(): Transaction = Transaction(
        transactionId = this.transactionId,
        timestamp = this.timestamp,
        type = this.type,
        category = this.category,
        account = this.account,
        memo = this.memo,
        originalCurrency = this.originalCurrency,
        originalAmount = this.originalAmount
    )

}

fun List<DatabaseTransaction>.toDomain(): List<Transaction> = map { it.toDomain() }