package com.kaeonx.moneymanager.customclasses

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toFormattedString(pattern: String): String {
    val dateFormat = DateFormat.getDateTimeInstance() as SimpleDateFormat
    dateFormat.applyPattern(pattern)
    return dateFormat.format(time)
}