package com.kaeonx.moneymanager.work

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import kotlinx.coroutines.delay
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

private const val TAG = "UploadDataWorker"

class UploadDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        internal const val WORK_NAME = "MMUploadDataWorker"
        internal const val LAST_SUCCESS_MILLIS = "MMLastSuccessMillis"
    }

    override suspend fun doWork(): Result {

        fun buildUserFilePath(uid: String): String {
            return App.context.filesDir.path + "/auto_backup/users/$uid.json"
        }

        fun saveRootToFile(filePath: String, jsonString: String): Boolean {
            val file = File(filePath)
            return try {
                val parentDir = File(file.parent!!)
                if (!parentDir.exists()) parentDir.mkdirs()
                BufferedWriter(FileWriter(file)).use { it.write(jsonString) }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        try {
            Log.d(TAG, "doWork() called and starting in try block")
            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid.also {
                Log.d(
                    TAG,
                    "doWork(): currentUserId succeeded with currentUser $it"
                )
            }
            UserDatabase.getInstance(currentUserId)  // ensures there is a correct INSTANCE
            UserDatabase.dropInstance()  // closes the database instance

            delay(3000L)  // delay just in case the closing takes time

            val dbFile = File(App.context.filesDir.path + "/databases/user_database_$currentUserId")

            val storage = Firebase.storage
            val storageRef = storage.reference.child("user_data")

            val userRef = storageRef.child("user_database_$currentUserId")
            val uploadTask = userRef.putFile(Uri.fromFile(dbFile), storageMetadata {
                setCustomMetadata("uploadStartMillis", System.currentTimeMillis().toString())
            })
            uploadTask.addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "uploadTask: failed, with exception $exception, message ${exception.message}, cause ${exception.cause}, stacktrace ${exception.stackTrace.joinToString(
                        "\n"
                    )}"
                )
            }
            uploadTask.addOnSuccessListener { taskSnapshot ->
                Log.d(
                    TAG,
                    "uploadTask: succeeded with taskSnapshot $taskSnapshot, bytesTransferred is ${taskSnapshot.bytesTransferred}"
                )
            }
            uploadTask.addOnCanceledListener {
                Log.d(TAG, "uploadTask: cancelled")
            }
            uploadTask.addOnCompleteListener { task ->  // SHLD BE USELESS
                Log.d(TAG, "uploadTask: completed with task $task, task.result is ${task.result}")
            }
            uploadTask.addOnPausedListener { taskSnapshot ->
                Log.d(
                    TAG,
                    "uploadTask: paused with taskSnapshot $taskSnapshot, bytesTransferred is ${taskSnapshot.bytesTransferred}"
                )
            }
            uploadTask.addOnProgressListener { taskSnapshot ->
                Log.d(
                    TAG,
                    "uploadTask: progress listener with taskSnapshot $taskSnapshot, bytesTransferred is ${taskSnapshot.bytesTransferred}"
                )
            }

        } catch (e: Exception) {
            Result.retry()
        }

//
//        val database = getDatabase(applicationContext)
//        val userRepository = VideosRepository(database)
//
//        return try {
//            repository.refreshVideos()
//            Result.success()
//        } catch (e: HttpException) {
//            Result.retry()
//        }
        val output = workDataOf(LAST_SUCCESS_MILLIS to 123123L)
        return Result.success(output)

        /*
        WorkManager.getInstance(myContext).getWorkInfoByIdLiveData(mathWork.id)
        .observe(this, Observer { info ->
            if (info != null && info.state.isFinished) {
                val myResult = info.outputData.getInt(KEY_RESULT,
                      myDefaultValue)
                // ... do something with the result ...
            }
        })
         */
    }

}