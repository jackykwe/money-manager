package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category (
    val type: String,
    var name: String = "",
    var iconHex: String = "F02D6",
    var colourString: String = "Black"
) : Parcelable