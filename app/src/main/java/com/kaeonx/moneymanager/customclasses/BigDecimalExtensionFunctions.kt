package com.kaeonx.moneymanager.customclasses

import com.kaeonx.moneymanager.fragments.transactions.TransactionsBSDFViewModel
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Used in **arithmetic calculations** to handle rounding, truncating of zeroes,
 * and displaying amounts properly in 2 decimal places.
 */
fun BigDecimal.toDisplayStringNullable(): String? {
    if (compareTo(BigDecimal.ZERO) == 0) return null

    val twoDP = setScale(TransactionsBSDFViewModel.MAX_DP, RoundingMode.HALF_UP)
    if (twoDP.compareTo(BigDecimal.ZERO) == 0) return null  // fixes weird 0.00 being returned as 0.00 instead of 0 error

    val twoDPSTZ = twoDP.stripTrailingZeros()
    return if (twoDPSTZ.scale() <= 0) {  // twoDPSTZ is a whole number, return integer representation (twoDPSTZ)
        twoDPSTZ.toPlainString()
    } else {  // twoDPSTZ is not a whole number, return 2 DP representation (twoDP)
        twoDP.toPlainString()
    }
}

/**
 * Used in **arithmetic calculations** to handle rounding, truncating of zeroes,
 * and displaying amounts properly in 2 decimal places.
 */
fun BigDecimal.toDisplayString(): String {
////        return if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) "0" else bigDecimal.setScale(MAX_DP, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    if (compareTo(BigDecimal.ZERO) == 0) return "0"

    val twoDP = setScale(TransactionsBSDFViewModel.MAX_DP, RoundingMode.HALF_UP)
    if (twoDP.compareTo(BigDecimal.ZERO) == 0) return "0"  // fixes weird 0.00 being returned as 0.00 instead of 0 error

    val twoDPSTZ = twoDP.stripTrailingZeros()
    return if (twoDPSTZ.scale() <= 0) {
        // twoDPSTZ is a whole number, return integer representation (twoDPSTZ)
        twoDPSTZ.toPlainString()
    } else {
        // twoDPSTZ is not a whole number, return 2 DP representation (twoDP)
        twoDP.toPlainString()
    }
}

/**
 * Used to **convert currency values**. The 'this' value must be a foreign currency value!
 */
internal fun BigDecimal.convertFrom(foreignCurrency: String, homeCurrency: String): BigDecimal {
    // value(home) x rate = value(foreign)
    // value(foreign) / rate = value(home)

    val rate = TODO()
//    val rate = CurrencyHandler.getConversionRateFromCurrentHomeCurrencyOf(foreignCurrency).toBigDecimal()
    return this.divide(rate, MathContext(9, RoundingMode.HALF_UP))
}

//// Entrance to currency conversion
//internal fun getConversionRateFromCurrentHomeCurrencyOf(foreignCurrency: String): String {
//    val currentSetHomeCurrency = PreferenceDS.getString2("ccc_home_currency")
//    return if (loadedCurrencyTable.isEmpty() || loadedBaseCurrency != currentSetHomeCurrency) {
//        if (JSONHandler.readCurrencyTable(currentSetHomeCurrency)) {  // managed to fetch from .json file. Read from it.
//            loadedCurrencyTable[foreignCurrency] ?: throw IllegalStateException("Unable to find foreignCurrency $foreignCurrency in loadedCurrencyTable")
//        } else {  // unable to fetch from .json file. Download.
//            if (fetchAndSaveLatestConversionTable(currentSetHomeCurrency)) {  // successfully downloaded, saved and loaded
//                loadedCurrencyTable[foreignCurrency] ?: throw IllegalStateException("Unable to find foreignCurrency $foreignCurrency in loadedCurrencyTable")
//            } else {
//                throw Exception("Something went really wrong. Unable to download, or unable to save, or unable to load.")
//            }
//        }
//    } else {
//        loadedCurrencyTable[foreignCurrency] ?: throw IllegalStateException("Unable to find foreignCurrency $foreignCurrency in loadedCurrencyTable")
//    }
//}
