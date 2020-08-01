package com.kaeonx.moneymanager.handlers

import com.kaeonx.moneymanager.fragments.transactions.TransactionsBSDFViewModel
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class CurrencyHandler private constructor() {
    companion object {

        fun displayAmountNullable(bigDecimal: BigDecimal): String? {
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return null

            val twoDP = bigDecimal.setScale(TransactionsBSDFViewModel.MAX_DP, RoundingMode.HALF_UP)
//            if (twoDP.compareTo(BigDecimal.ZERO) == 0) return null  // fixes weird 0.00 being returned as 0.00 instead of 0

            val twoDPSTZ = twoDP.stripTrailingZeros()
            return if (twoDPSTZ.scale() <= 0) {  // twoDPSTZ is a whole number, return integer representation (twoDPSTZ)
                twoDPSTZ.toPlainString()
            } else {  // twoDPSTZ is not a whole number, return 2 DP representation (twoDP)
                twoDP.toPlainString()
            }
        }

        fun displayAmount(bigDecimal: BigDecimal): String {
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return "0"

            val twoDP = bigDecimal.setScale(TransactionsBSDFViewModel.MAX_DP, RoundingMode.HALF_UP)
//            if (twoDP.compareTo(BigDecimal.ZERO) == 0) return "0"  // fixes weird 0.00 being returned as 0.00 instead of 0

            val twoDPSTZ = twoDP.stripTrailingZeros()
            return if (twoDPSTZ.scale() <= 0) {
                // twoDPSTZ is a whole number, return integer representation (twoDPSTZ)
                twoDPSTZ.toPlainString()
            } else {
                // twoDPSTZ is not a whole number, return 2 DP representation (twoDP)
                twoDP.toPlainString()
            }
        }

        private fun displayAmountAsBigDecimal(bigDecimal: BigDecimal): BigDecimal {
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO

            val twoDP = bigDecimal.setScale(TransactionsBSDFViewModel.MAX_DP, RoundingMode.HALF_UP)
            val twoDPSTZ = twoDP.stripTrailingZeros()
            return if (twoDPSTZ.scale() <= 0) twoDPSTZ else twoDP
        }

        fun convertAmount(
            bigDecimal: BigDecimal,
            foreignCurrencySrc: String,
            homeCurrencyDst: String
        ): BigDecimal {
            // value(home) x rate = value(foreign)
            // value(foreign) / rate = value(home)
            val rate =
                BigDecimal("2")  // TODO: get rate. If fail, download, return placeholder (times 0) first
            return displayAmountAsBigDecimal(
                bigDecimal.divide(
                    rate,
                    MathContext(9, RoundingMode.HALF_UP)
                )
            )
        }
    }
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
