package com.kaeonx.moneymanager.customclasses

// Functions like a named lambda
class GenericOnClickListener(val clickListener: () -> Unit) {
    fun onClick() = clickListener()
}