package com.kaeonx.moneymanager.fragments.importexport.iehandlers

import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.parseJsonDataException
import com.kaeonx.moneymanager.userrepository.domain.Budget
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONArray

internal class IEBudgetsHandler private constructor() {

    companion object {

        private val moshiAdapter by lazy {
            Moshi
                .Builder()
                .build()
                .adapter<List<Budget>>(
                    Types.newParameterizedType(List::class.java, Budget::class.java)
                )
                .nonNull()
        }

        // JSONArray --> String --> List<Budget>
        internal fun jsonArrayToList(jsonArray: JSONArray): List<Budget> = try {
            moshiAdapter.fromJson(jsonArray.toString()) ?: listOf()
        } catch (e: JsonDataException) {
            throw IllegalStateException("found malformed Budget\n${parseJsonDataException(e.message)}")
        }

        // List<Budget> --> String --> JSONArray
        internal fun listToJsonArray(list: List<Budget>): JSONArray =
            JSONArray(moshiAdapter.toJson(list))

    }

}