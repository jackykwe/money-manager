package com.kaeonx.moneymanager.activities

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream

internal class CloudMetadata private constructor(
    val lastKnownLoginMillis: Long
) {

    internal class Builder(
        private val lastKnownLoginMillis: Long
    ) {

        internal fun toByteInputStream(): ByteArrayInputStream =
            JSONObject().apply {
                put("last_known_login_millis", lastKnownLoginMillis)
            }.toString().byteInputStream()

    }

    companion object {

        internal suspend fun fromInputStream(inputStream: InputStream): CloudMetadata {
            return withContext(Dispatchers.IO) {
                val jsonObject = JSONObject(inputStream.bufferedReader().readText())
                return@withContext CloudMetadata(
                    lastKnownLoginMillis = jsonObject.getLong("last_known_login_millis")
                )
            }
        }

    }

}