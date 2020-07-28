package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.userrepository.database.DatabaseExpensesCategory
import com.kaeonx.moneymanager.userrepository.database.DatabaseIncomeCategory
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val type: String,
    var name: String,
    var iconHex: String,
    var colourString: String
) : Parcelable {

    fun toIconDetail(): IconDetail = IconDetail(
        iconHex = this.iconHex,
        iconBGColourString = this.colourString,
        iconRingColourString = "TRANSPARENT"
    )

    fun toDatabaseIncome(): DatabaseIncomeCategory =
        DatabaseIncomeCategory(
            type = this.type,
            name = this.name,
            iconHex = this.iconHex,
            colourString = this.colourString
        )

    fun toDatabaseExpenses(): DatabaseExpensesCategory =
        DatabaseExpensesCategory(
            type = this.type,
            name = this.name,
            iconHex = this.iconHex,
            colourString = this.colourString
        )
}