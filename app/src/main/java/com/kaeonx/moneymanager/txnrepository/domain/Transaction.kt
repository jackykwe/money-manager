package com.kaeonx.moneymanager.txnrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.txnrepository.database.DatabaseTxn
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    var txnId: Long? = null,
    var timestamp: Long = 0L,
    var timeZone: String = "",
    var type: String = "",
    var category: String = "",
    var account: String = "",
    var memo: String = "",
    var originalCurrency: String = "",
    var originalAmount: String = "") : Parcelable

fun Transaction.toDatabase(): DatabaseTxn {
    return DatabaseTxn(
        timestamp = this.timestamp,
        timeZone = this.timeZone,
        type = this.type,
        category = this.category,
        account = this.account,
        memo = this.memo,
        originalCurrency = this.originalCurrency,
        originalAmount = this.originalAmount
    )
}