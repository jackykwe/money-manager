package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    var name: String = "",
    var colourString: String = "White"
) : Parcelable