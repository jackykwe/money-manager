package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val type: String,
    var name: String,
    var iconHex: String,
    var colourString: String
) : Parcelable {

    fun toIconDetail(): IconDetail = IconDetail(iconHex, colourString, "TRANSPARENT")

}