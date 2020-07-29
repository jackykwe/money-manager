package com.kaeonx.moneymanager.handlers

import android.text.Html

class IconHandler private constructor() {
    companion object {

        // Number of reported icons on cheat-sheet minus one (5.4.55)
        private const val MAX_SUPPORTED_HEX = 5455

        fun getDisplayHex(iconHex: String): String {
            // iconHex looks like Fxxxx
            val internalId = iconHex.substring(0 until 5)
            return Html.fromHtml("&#x$internalId;", Html.FROM_HTML_MODE_LEGACY).toString()
        }

        fun iconHexToInt(iconHex: String): Int {
            // id looks like Fxxxx
            return iconHex.substring(1 until 5).toInt(16)
        }

        fun iconHexIsValid(iconHex: String): Boolean {
            return iconHex.contains(Regex("^F[A-F0-9]{4}$")) && iconHexToInt(iconHex) in 1..MAX_SUPPORTED_HEX
        }
    }
}
