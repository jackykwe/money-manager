package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.userrepository.database.DatabaseCategory
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val categoryId: Int?,
    val type: String,
    var name: String,
    var iconHex: String,
    var colourString: String
) : Parcelable {

    fun toIconDetail(): IconDetail = IconDetail(
        iconHex = this.iconHex,
        iconBGColourString = this.colourString,
        iconRingColourString = "INVISIBLE"
    )

    fun toDatabase(): DatabaseCategory =
        DatabaseCategory(
            categoryId = this.categoryId ?: 0,
            type = this.type,
            name = this.name,
            iconHex = this.iconHex,
            colourString = this.colourString
        )
}