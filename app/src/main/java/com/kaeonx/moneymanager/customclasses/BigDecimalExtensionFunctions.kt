package com.kaeonx.moneymanager.customclasses

import com.kaeonx.moneymanager.fragments.transactions.TransactionsBSDFViewModel
import java.math.BigDecimal
import java.math.RoundingMode

//
//fun BigDecimal.dimalDisplayNullable(bigDecimal: BigDecimal): String? {
//    if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return null
//
//    val twoDP = bigDecimal.setScale(TBSDFViewModel.MAX_DP, RoundingMode.HALF_UP)
//    if (twoDP.compareTo(BigDecimal.ZERO) == 0) return null  // fixes weird 0.00 being returned as 0.00 instead of 0 error
//
//    val twoDPSTZ = twoDP.stripTrailingZeros()
//    return if (twoDPSTZ.scale() <= 0) {  // twoDPSTZ is a whole number, return integer representation (twoDPSTZ)
//        twoDPSTZ.toPlainString()
//    } else {  // twoDPSTZ is not a whole number, return 2 DP representation (twoDP)
//        twoDP.toPlainString()
//    }
//}

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