package com.kaeonx.moneymanager.adapters

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
@BindingAdapter("iconRing_tint")
fun ImageView.setIconRingTint(colourString: String) {
    imageTintList = ColourHandler.getColorStateList(colourString)
}

@BindingAdapter("iconBG_tint")
fun ImageView.setIconBGTint(colourString: String) {
    imageTintList = ColourHandler.getColorStateList(colourString)
}

@BindingAdapter("iconTV_text")
fun TextView.setIconTVText(iconHex: String) {
    text = IconHandler.getDisplayHex(iconHex)
}

////////////////////////////////////////////////////////////////////////////////
/**
 * Generic
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("error_text")
fun TextInputLayout.setErrorText(errorString: String?) {
    error = errorString
}