package com.kaeonx.moneymanager.userrepository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaeonx.moneymanager.userrepository.domain.Transaction

@Entity(tableName = "txn_table")
data class DatabaseTxn(
    @PrimaryKey(autoGenerate = true) val txnId: Long = 0L,
    val timestamp: Long,
    val type: String,
    val category: String,
    val account: String,
    val memo: String,
    @ColumnInfo(name = "original_currency") val originalCurrency: String,
    @ColumnInfo(name = "original_amount") val originalAmount: String)

fun List<DatabaseTxn>.toDomain(): List<Transaction> {
    return map {
        Transaction(
            txnId = it.txnId,
            timestamp = it.timestamp,
            type = it.type,
            category = it.category,
            account = it.account,
            memo = it.memo,
            originalCurrency = it.originalCurrency,
            originalAmount = it.originalAmount
        )
    }
}

// TODO: Returning subsets of columns https://developer.android.com/training/data-storage/room/accessing-data#query-subset-cols