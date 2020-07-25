package com.kaeonx.moneymanager.customclasses

import android.text.Html

fun String.toIconHex(): String {
    // String looks like Fxxxx
    val internalId = substring(0 until 5)
    return Html.fromHtml("&#x$internalId;", Html.FROM_HTML_MODE_LEGACY).toString()
}
