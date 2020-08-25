package com.kaeonx.moneymanager.handlers

import com.kaeonx.moneymanager.xerepository.XERepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class CurrencyHandler private constructor() {
    companion object {

        private fun largeValueFormatter(bigDecimal: BigDecimal): String {
            return when {
                bigDecimal >= BigDecimal("1E7") -> DecimalFormat("0.00E0").apply {
                    roundingMode = RoundingMode.HALF_UP
                }.format(bigDecimal)
                bigDecimal < BigDecimal.ZERO -> "- " + bigDecimal.toPlainString().substring(1)
                else -> bigDecimal.toPlainString()
            }
        }

        internal fun displayAmount(bigDecimal: BigDecimal): String {
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return "0"

            val twoDP = bigDecimal.setScale(2, RoundingMode.HALF_UP)
            // if (twoDP.compareTo(BigDecimal.ZERO) == 0) return null  // fixes weird 0.00 being returned as 0.00 instead of 0

            val twoDPSTZ = twoDP.stripTrailingZeros()
            return if (twoDPSTZ.scale() <= 0) {
                largeValueFormatter(twoDPSTZ)  // twoDPSTZ is a whole number, return integer representation (twoDPSTZ)
            } else {
                largeValueFormatter(twoDP)  // twoDPSTZ is not a whole number, return 2 DP representation (twoDP)
            }
        }

        internal fun largePercentFormatter(bigDecimal: BigDecimal): String {
            return if (bigDecimal >= BigDecimal("1E6")) {
                DecimalFormat("'('0.0E0'%)'").apply {
                    roundingMode = RoundingMode.HALF_UP
                }.format(bigDecimal)
            } else {
                "(${bigDecimal.toPlainString()}%)"
            }
        }

//        private fun displayAmountAsBigDecimal(bigDecimal: BigDecimal): BigDecimal {
//            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
//            val twoDP = bigDecimal.setScale(TransactionsBSDFViewModel.MAX_DP, RoundingMode.HALF_UP)
//            val twoDPSTZ = twoDP.stripTrailingZeros()
//            return if (twoDPSTZ.scale() <= 0) twoDPSTZ else twoDP
//        }

//        internal fun convertAmount(
//            bigDecimal: BigDecimal,
//            foreignCurrencySrc: String,
//            homeCurrencyDst: String
//        ): BigDecimal {
//            // value(home) x rate = value(foreign)
//            // value(foreign) / rate = value(home)
//            val rateString = XERepository.getInstance().xeRows.value!!
//                .find { it.foreignCurrency == foreignCurrencySrc && it.baseCurrency == homeCurrencyDst }  // This second condition is just a sanity check; not strictly needed, because xeRows should always be in homeCurrency.
//                ?.rate
//            return when (rateString) {
//                null -> BigDecimal.ZERO
//                else -> bigDecimal.divide(
//                    BigDecimal(rateString),
//                    2,
//                    RoundingMode.HALF_UP
//                )
//            }
//        }

        internal fun convertAmountViaSGDProxy(
            bigDecimal: BigDecimal,
            foreignCurrencySrc: String,
            foreignCurrencyDst: String
        ): BigDecimal {
            // value(Src) / rate(SGDSrc) = value(SGD)
            // value(SGD) x rate(SGDDst) = value(Dst)
            val rateSGDSrc: String?
            val rateSGDDst: String?
            XERepository.getInstance().xeRows.value!!.run {
                rateSGDSrc =
                    find { it.foreignCurrency == foreignCurrencySrc && it.baseCurrency == "SGD" }  // This second condition is just a sanity check; not strictly needed, because xeRows should always be in homeCurrency.
                        ?.rate
                rateSGDDst =
                    find { it.foreignCurrency == foreignCurrencyDst && it.baseCurrency == "SGD" }  // This second condition is just a sanity check; not strictly needed, because xeRows should always be in homeCurrency.
                        ?.rate
            }
            return if (rateSGDSrc == null || rateSGDDst == null) BigDecimal.ZERO else {
                bigDecimal.times(BigDecimal(rateSGDDst)).divide(
                    BigDecimal(rateSGDSrc),
                    2,
                    RoundingMode.HALF_UP
                )
            }
        }

    }
}