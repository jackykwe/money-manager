package com.kaeonx.moneymanager.importexport

import android.content.ContentResolver
import android.content.Intent
import java.io.*

internal class IEFileHandler private constructor() {

    companion object {

        internal const val OUTPUT_TO_FILE = 1
        internal const val READ_FROM_FILE = 2

        /**
         * @param fileName Without .json
         */
        internal fun constructWriteFileIntent(fileName: String): Intent =
            Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, "$fileName.json")
            }

        internal fun constructReadFileIntent(): Intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
            }

        internal fun readJSONString(contentResolver: ContentResolver, data: Intent?): String {
            // Template from https://developer.android.com/training/data-storage/shared/documents-files#open
            // Choice of and usage of BufferedReader and InputStreamReader derived from
            // official Android documentation.
            if (data == null) throw IOException("data (Intent) is null")
            if (data.data == null) throw IOException("data.data (Intent.data) is null")
            val stringBuilder = StringBuilder()
            contentResolver.openInputStream(data.data!!).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line + "\n")
                        line = reader.readLine()
                    }
                }
            }
            return stringBuilder.trim().toString()
        }

        internal fun writeJSONString(
            contentResolver: ContentResolver,
            data: Intent?,
            contentToWrite: String
        ) {
            // Template from https://developer.android.com/training/data-storage/shared/documents-files#edit
            // Choice of and usage of BufferedWriter and OutputStreamWriter derived from
            // official Android documentation.
            if (data == null) throw IOException("data (Intent) is null")
            if (data.data == null) throw IOException("data.data (Intent.data) is null")
            contentResolver.openOutputStream(data.data!!)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(contentToWrite)
                }
            }
        }
    }
}