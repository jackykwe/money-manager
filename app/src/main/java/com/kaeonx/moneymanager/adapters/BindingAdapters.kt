package com.kaeonx.moneymanager.adapters

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.IconHandler

////////////////////////////////////////////////////////////////////////////////
/**
 * For icon_transaction.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("iconTV_text")
fun TextView.setIconTVText(iconHex: String) {
    text = IconHandler.getDisplayHex(iconHex)
}

@BindingAdapter("iconTV_textColour")
fun TextView.setIconTVTextColour(colourFamily: String) {
    when (colourFamily) {
        "White" -> setTextColor(ColourHandler.getSpecificColourObjectOf("Black"))
        "TRANSPARENT" -> setTextColor(ColourHandler.getSpecificColourObjectOf("Red,500"))
        else -> setTextColor(ColourHandler.getSpecificColourObjectOf("White"))
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * Generic
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("colour_string_tint")
fun ImageView.setColourStringTint(colourFamily: String) {
    imageTintList = ColourHandler.getColorStateListOf(colourFamily)
}

@BindingAdapter("color_tint")
fun ImageView.setColorTint(color: Int) {
    imageTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("error_text")
fun TextInputLayout.setErrorText(errorString: String?) {
    error = errorString
}