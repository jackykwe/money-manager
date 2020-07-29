package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    val accountId: Int?,
    var name: String,
    var colourString: String
) : Parcelable {

    fun toIconDetail(): IconDetail = IconDetail(
        iconHex = "F02D6",
        iconBGColourString = "TRANSPARENT",
        iconRingColourString = this.colourString
    )

}
