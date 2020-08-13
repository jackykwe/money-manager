package com.kaeonx.moneymanager.fragments.importexport.iehandlers

import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.parseJsonDataException
import com.kaeonx.moneymanager.userrepository.domain.Category
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONArray

internal class IECategoriesHandler private constructor() {

    companion object {

        private val moshiAdapter by lazy {
            Moshi
                .Builder()
                .build()
                .adapter<List<Category>>(
                    Types.newParameterizedType(List::class.java, Category::class.java)
                )
                .nonNull()
        }

        // JSONArray --> String --> List<Category>
        internal fun jsonArrayToList(jsonArray: JSONArray): List<Category> = try {
            moshiAdapter.fromJson(jsonArray.toString()) ?: listOf()
        } catch (e: JsonDataException) {
            throw IllegalStateException("found malformed Category\n${parseJsonDataException(e.message)}")
        }

        // List<Category> --> String --> JSONArray
        internal fun listToJsonArray(list: List<Category>): JSONArray =
            JSONArray(moshiAdapter.toJson(list))

    }

}