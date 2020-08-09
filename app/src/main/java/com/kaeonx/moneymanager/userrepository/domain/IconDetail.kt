package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IconDetail(
    val iconHex: String,
    val iconBGColourString: String,
    val iconRingColourString: String
) : Parcelable