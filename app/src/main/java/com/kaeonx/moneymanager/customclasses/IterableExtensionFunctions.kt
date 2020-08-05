package com.kaeonx.moneymanager.customclasses

import java.math.BigDecimal

// Courtesy of https://bezkoder.com/kotlin-sum-sumby-sumbydouble-bigdecimal-list-map-example/
internal fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this@sumByBigDecimal) {
        sum = sum.plus(selector(element))
    }
    return sum
}