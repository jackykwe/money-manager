package com.kaeonx.moneymanager.handlers

import android.text.Html

class IconHandler private constructor() {
    companion object {

        fun getDisplayHex(iconHex: String): String {
            // iconHex looks like Fxxxx
            val internalId = iconHex.substring(0 until 5)
            return Html.fromHtml("&#x$internalId;", Html.FROM_HTML_MODE_LEGACY).toString()
        }

    }
}
