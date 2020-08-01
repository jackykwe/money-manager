package com.kaeonx.moneymanager.xerepository.network

import com.kaeonx.moneymanager.xerepository.database.DatabaseXERow
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects are responsible for parsing responses from the server or formatting objects
 * to send to the server. You should convert these to domain objects before using them.
 */

// This is how the online API represents the data
@JsonClass(generateAdapter = true)
data class NetworkXEContainer(
    val rates: Map<String, String>,
    val base: String,
    val date: String
)

//@JsonClass(generateAdapter = true)
//data class NetworkXERow(
//    val foreignCurrency: String,
//    val rate: String
//)

// Require Array instead of list for spread operator (*) to work (see XERepository)
fun NetworkXEContainer.toDatabase(updateMillis: Long): Array<DatabaseXERow> {
    return this.rates.map {
        DatabaseXERow(
            baseCurrency = this.base,
            foreignCurrency = it.key,
            date = this.date,
            rate = it.value,
            updateMillis = updateMillis  // Time of save
        )
    }.toTypedArray()
}