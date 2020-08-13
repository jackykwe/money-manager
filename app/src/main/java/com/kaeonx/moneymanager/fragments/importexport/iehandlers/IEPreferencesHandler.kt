package com.kaeonx.moneymanager.fragments.importexport.iehandlers

import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.parseJsonDataException
import com.kaeonx.moneymanager.userrepository.domain.Preference
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONArray

internal class IEPreferencesHandler private constructor() {

    companion object {

        private val moshiAdapter by lazy {
            Moshi
                .Builder()
                .build()
                .adapter<List<Preference>>(
                    Types.newParameterizedType(List::class.java, Preference::class.java)
                )
                .nullSafe()
        }

        // JSONArray --> String --> List<Preference>
        internal fun jsonArrayToList(jsonArray: JSONArray): List<Preference> = try {
            moshiAdapter.fromJson(jsonArray.toString()) ?: listOf()
        } catch (e: JsonDataException) {
            throw IllegalStateException("found malformed Setting\n${parseJsonDataException(e.message)}")
        }

        // List<Preference> --> String --> JSONArray
        internal fun listToJsonArray(list: List<Preference>): JSONArray =
            JSONArray(moshiAdapter.toJson(list))

    }

}