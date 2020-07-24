package com.kaeonx.moneymanager.xerepository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.kaeonx.moneymanager.xerepository.domain.XERow

@Entity(tableName = "currency_conversion_table", primaryKeys = ["base_currency", "foreign_currency"])
data class DatabaseXERow(
    @ColumnInfo(name = "base_currency") val baseCurrency: String,
    @ColumnInfo(name = "foreign_currency") val foreignCurrency: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "rate") val rate: String,
    @ColumnInfo(name = "update_time") val updateTime: Long = System.currentTimeMillis() // todo: change to network call time
) {
    // base_currency * rate = foreign_currency
}

fun List<DatabaseXERow>.toDomain(): List<XERow> {
    return map {
        XERow(
            baseCurrency = it.baseCurrency,
            foreignCurrency = it.foreignCurrency,
            rate = it.rate,
            updateTime = it.updateTime
        )
    }
}

// TODO: Returning subsets of columns https://developer.android.com/training/data-storage/room/accessing-data#query-subset-cols