package com.kaeonx.moneymanager.xerepository.domain

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 */

data class XERow(
    val baseCurrency: String,
    val foreignCurrency: String,
    val rate: String,
    val updateTime: Long
)