package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.DatabaseBudget
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Budget(
    val category: String,
    val originalCurrency: String,
    val originalAmount: String
) : Parcelable {

    fun toIconDetail(): IconDetail {
        if (category == "Overall") return IconDetail("F04A0", "Black", "TRANSPARENT")
        val categoryObj = UserRepository.getInstance().categories.value!!
            .find { it.name == this.category && it.type == "Expenses" }
            ?: throw IllegalStateException("Tried to edit budget for category that does not exist.")
        return IconDetail(
            iconHex = categoryObj.iconHex,
            iconBGColourString = categoryObj.colourString,
            iconRingColourString = "TRANSPARENT"
        )
    }

    fun toDatabase(): DatabaseBudget = DatabaseBudget(
        category = category,
        originalCurrency = originalCurrency,
        originalAmount = originalAmount
    )
}