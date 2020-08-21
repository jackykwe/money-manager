package com.kaeonx.moneymanager.fragments.importexport.iehandlers

import android.content.ContentResolver
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

internal class IEFileHandler private constructor() {

    companion object {

        internal const val OUTPUT_TO_FILE = 1
        internal const val READ_FROM_FILE = 2
        internal const val UPLOAD_DATA = 2
        internal const val DELETE_DATA = 3

        /**
         * @param fileName Without .json
         */
        internal fun constructWriteFileIntent(fileName: String): Intent =
            Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_TITLE, fileName)
            }

        internal fun constructReadFileIntent(): Intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }

        internal fun readJSONString(contentResolver: ContentResolver, data: Intent?): String {
            // Template from https://developer.android.com/training/data-storage/shared/documents-files#open
            // Choice of and usage of BufferedReader and InputStreamReader derived from
            // official Android documentation.
            if (data == null) throw IllegalStateException("data (Intent) is null")
            if (data.data == null) throw IllegalStateException("data.data (Intent.data) is null")
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
            if (data == null) throw IllegalStateException("data (Intent) is null")
            if (data.data == null) IllegalStateException("data.data (Intent.data) is null")
            contentResolver.openOutputStream(data.data!!)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(contentToWrite)
                }
            }
        }

        internal fun parseJsonDataException(message: String?): String? {
            return message?.let { msg ->
                val element = msg.substringAfter("JSON name ").takeIf { it != msg }
                    ?.substringBefore(")").takeIf { it != msg }
                    ?.replace("'", "\"")
                val index = msg.substringAfter("$[").takeIf { it != msg }
                    ?.substringBefore("]").takeIf { it != msg }
                if (element == null || index == null) "unknown reason\nPlease report this bug."
                else "attribute $element missing at index $index"
            }
        }

        /**
         * Saves a JSON String into a .json file specified by [filePath]. Internally checks
         * if the [filePath]'s parent directory exists. If not, the directories are created.
         * @param filePath A full path including the destination JSON file name with extension
         * @param jsonString A JSON string obtained from `JSONObject.toString()`.
         * @return `true` if the write was successful, else `false`.
         */
        internal suspend fun saveRootToFile(filePath: String, jsonString: String): Boolean {
            return withContext(Dispatchers.IO) {
                val file = File(filePath)
                try {
                    val parentDir = File(file.parent!!)
                    if (!parentDir.exists()) parentDir.mkdirs()
                    BufferedWriter(FileWriter(file)).use { it.write(jsonString) }
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }
    }
}