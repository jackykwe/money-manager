package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val type: String,
    var name: String = "?",
    var iconHex: String = "F02D6",  // Default is defined here
    var colourString: String = "Black"  // Default is defined here
) : Parcelable {

    fun toIconDetail(): IconDetail = IconDetail(iconHex, colourString, "transparent")

}