package com.kaeonx.moneymanager.fragments.importexport.iehandlers

import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.parseJsonDataException
import com.kaeonx.moneymanager.userrepository.domain.Account
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONArray

internal class IEAccountsHandler private constructor() {

    companion object {

        private val moshiAdapter by lazy {
            Moshi
                .Builder()
                .build()
                .adapter<List<Account>>(
                    Types.newParameterizedType(List::class.java, Account::class.java)
                )
                .nonNull()
        }

        // JSONArray --> String --> List<Account>
        internal fun jsonArrayToList(jsonArray: JSONArray): List<Account> = try {
            moshiAdapter.fromJson(jsonArray.toString()) ?: listOf()
        } catch (e: JsonDataException) {
            throw IllegalStateException("found malformed Account\n${parseJsonDataException(e.message)}")
        }

        // List<Account> --> String --> JSONArray
        internal fun listToJsonArray(list: List<Account>): JSONArray =
            JSONArray(moshiAdapter.toJson(list))

    }

}