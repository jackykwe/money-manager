package com.kaeonx.moneymanager.fragments.importexport.iehandlers

import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.parseJsonDataException
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.squareup.moshi.JsonDataException
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
        internal fun jsonArrayToList(jsonArray: JSONArray): List<Transaction> = try {
            moshiAdapter.fromJson(jsonArray.toString()) ?: listOf()
        } catch (e: JsonDataException) {
            throw IllegalStateException("found malformed Transaction\n${parseJsonDataException(e.message)}")
        }

        // List<Transaction> --> String --> JSONArray
        internal fun listToJsonArray(list: List<Transaction>): JSONArray =
            JSONArray(moshiAdapter.toJson(list))

    }

}