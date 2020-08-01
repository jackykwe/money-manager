package com.kaeonx.moneymanager.handlers

import com.kaeonx.moneymanager.fragments.transactions.TransactionsBSDFViewModel
import com.kaeonx.moneymanager.xerepository.XERepository
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
            val twoDPSTZ = twoDP.stripTrailingZeros()
            return if (twoDPSTZ.scale() <= 0) twoDPSTZ.toPlainString() else twoDP.toPlainString()
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
            val rateString = XERepository.getInstance().xeRows.value!!
                .find { it.baseCurrency == homeCurrencyDst && it.foreignCurrency == foreignCurrencySrc }
                ?.rate
            return when (rateString) {
                null -> BigDecimal("0.01")
                else -> displayAmountAsBigDecimal(
                    bigDecimal.divide(
                        BigDecimal(rateString),
                        MathContext(9, RoundingMode.HALF_UP)
                    )
                )
            }
        }
    }
}