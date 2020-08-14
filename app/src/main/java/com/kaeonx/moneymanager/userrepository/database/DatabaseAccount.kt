package com.kaeonx.moneymanager.userrepository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaeonx.moneymanager.userrepository.domain.Account

@Entity(tableName = "accounts_table")
data class DatabaseAccount(
    @PrimaryKey(autoGenerate = true) val accountId: Int,
    val name: String,
    @ColumnInfo(name = "colour_string") val colourString: String
)

fun List<DatabaseAccount>.toDomain(): List<Account> = map {
    Account(
        accountId = it.accountId,
        name = it.name,
        colourFamily = it.colourString
    )
}