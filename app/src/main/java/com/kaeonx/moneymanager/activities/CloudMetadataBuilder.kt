package com.kaeonx.moneymanager.activities

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream

internal data class CloudMetadata(
    internal val lastKnownLoginMillis: Long,
    internal val lastKnownOnline: Long,
    internal val lastKnownOnlineVersion: String
) {

    internal fun toByteInputStream(): ByteArrayInputStream =
        JSONObject().apply {
            put("l", lastKnownLoginMillis)
            put("o", lastKnownOnline)
            put("v", lastKnownOnlineVersion)
        }.toString().byteInputStream()

    companion object {

        internal suspend fun fromInputStream(inputStream: InputStream): CloudMetadata {
            return withContext(Dispatchers.IO) {
                val jsonObject = JSONObject(inputStream.bufferedReader().use { it.readText() })
                return@withContext CloudMetadata(
                    lastKnownLoginMillis = jsonObject.getLong("l"),
                    lastKnownOnline = jsonObject.getLong("o"),
                    lastKnownOnlineVersion = jsonObject.getString("v")
                )
            }
        }

    }

}