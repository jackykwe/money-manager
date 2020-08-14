package com.kaeonx.moneymanager.fragments.accounts

import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.handlers.ColourHandler

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_accounts_display.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("horizontalBarIV_tint")
fun ImageView.setHorizontalBarIVTint(colourFamily: String) {
    drawable.setTintList(ColourHandler.getColorStateListOf(colourFamily))
}

@BindingAdapter("accountNameTV_textColor")
fun TextView.setAccountNameTVTextColor(colourFamily: String) {
    when (colourFamily) {
        "White" -> setTextColor(ColourHandler.getSpecificColourObjectOf("Black"))
        "TRANSPARENT" -> setTextColor(ColourHandler.getSpecificColourObjectOf("Red,500"))
        else -> setTextColor(ColourHandler.getSpecificColourObjectOf("White"))
    }
}

@BindingAdapter("accountNameTV_text")
fun TextView.setAccountNameTVText(name: String) {
    text = if (name == "Add…") buildSpannedString { italic { append(name) } } else name
}