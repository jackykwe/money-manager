package com.kaeonx.moneymanager.xerepository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface XEDatabaseDao {
    /*
    @Insert
    fun insertXERow(databaseXeRow: DatabaseXERow)

    @Update
    fun updateXERow(databaseXeRow: DatabaseXERow)  // Key must be the same

    @Query("SELECT * FROM currency_conversion_table WHERE `key` = :key")
    fun getXERow(key: Long): DatabaseXERow

//    @Delete
//    fun deleteXERow(key: Long)

//    @Delete
//    fun deleteXERows(rows: List<XERow>): Int

    @Query("SELECT * FROM currency_conversion_table ORDER BY `key` DESC")
    fun getAllXERows(): LiveData<List<DatabaseXERow>>
    */

    @Query("DELETE FROM currency_conversion_table")
    suspend fun clearAllSavedData()

    // LiveData and suspend don't go together. Apparently, LiveData is already async.
    // "Asynchronous queries—queries that return instances of LiveData or Flowable — are exempt
    // from this rule because they asynchronously run the query on a background thread when needed."
    // Source: https://developer.android.com/training/data-storage/room/accessing-data
    @Query("SELECT * FROM currency_conversion_table")
    fun getAllXERows(): LiveData<List<DatabaseXERow>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(vararg databaseXERows: DatabaseXERow)

    // TODO: REPOPULATION HOLY F YES https://developer.android.com/training/data-storage/room/prepopulate#from-asset
    // TODO: Non Internet version of app? Future.
}