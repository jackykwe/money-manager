package com.kaeonx.moneymanager.customclasses

import java.util.*

// Use on timestamps
fun Long.toCalendar(): Calendar {
    val c = Calendar.getInstance()
    c.timeInMillis = this
    return c
}

fun Long.toFormattedString(format: String): String {
    // In current timezone.
    val c = Calendar.getInstance()
    c.timeInMillis = this
    return c.toFormattedString(format)
}