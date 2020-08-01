package com.kaeonx.moneymanager.xerepository.network

import com.kaeonx.moneymanager.xerepository.database.DatabaseXERow
import com.kaeonx.moneymanager.xerepository.domain.XERow
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
fun NetworkXEContainer.toDatabase(): Array<DatabaseXERow> {
    return this.rates.map {
        DatabaseXERow(
            baseCurrency = this.base,
            foreignCurrency = it.key,
            date = this.date,
            rate = it.value,
            updateMillis = System.currentTimeMillis() // TODO: TIEM OF NETWORK CALL
        )
    }.toTypedArray()
}

fun NetworkXEContainer.toDomain(): List<XERow> {
    return this.rates.map {
        XERow(
            baseCurrency = this.base,
            foreignCurrency = it.key,
            rate = it.value,
            updateMillis = System.currentTimeMillis()  // TODO: TIME OF NETWORK CALL
        )
    }
}
