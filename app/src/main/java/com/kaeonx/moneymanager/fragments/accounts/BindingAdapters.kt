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
fun ImageView.setHorizontalBarIVTint(colourString: String) {
    drawable.setTintList(ColourHandler.getColorStateListOf(colourString))
}

@BindingAdapter("accountNameTV_textColor")
fun TextView.setAccountNameTVTextColor(name: String) {
    setTextColor(
        if (name == "Add…") ColourHandler.getColorStateListOf("Black") else ColourHandler.getColorStateListOf(
            "White"
        )
    )
}

@BindingAdapter("accountNameTV_text")
fun TextView.setAccountNameTVText(name: String) {
    text = if (name == "Add…") buildSpannedString { italic { append(name) } } else name
}