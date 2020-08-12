package com.kaeonx.moneymanager.importexport

import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONArray

internal class IETransactionsHandler private constructor() {

    companion object {

        private val moshiAdapter by lazy {
            Moshi
                .Builder()
                .build()
                .adapter<List<Transaction>>(
                    Types.newParameterizedType(List::class.java, Transaction::class.java)
                )
                .nonNull()
        }

        // JSONArray --> String --> List<Transaction>
        internal fun jsonArrayToList(jsonArray: JSONArray): List<Transaction> =
            moshiAdapter.fromJson(jsonArray.toString()) ?: listOf()

        // List<Transaction> --> String --> JSONArray
        internal fun listToJsonArray(list: List<Transaction>): JSONArray =
            JSONArray(moshiAdapter.toJson(list))

    }

}