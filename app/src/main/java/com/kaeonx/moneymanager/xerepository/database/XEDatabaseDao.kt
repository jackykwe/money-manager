package com.kaeonx.moneymanager.xerepository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface XEDatabaseDao {

    @Query("DELETE FROM currency_conversion_table")
    suspend fun clearAllSavedData()

    // LiveData and suspend don't go together. Apparently, LiveData is already async.
    // "Asynchronous queries—queries that return instances of LiveData or Flowable — are exempt
    // from this rule because they asynchronously run the query on a background thread when needed."
    // Source: https://developer.android.com/training/data-storage/room/accessing-data
    @Query("SELECT * FROM currency_conversion_table")
    fun getAllXERows(): LiveData<List<DatabaseXERow>>

    @Query("SELECT * FROM currency_conversion_table WHERE base_currency = :baseCurrency")
    fun getXERows(baseCurrency: String): LiveData<List<DatabaseXERow>>

    @Query("SELECT * FROM currency_conversion_table WHERE base_currency = :baseCurrency")
    suspend fun getXERowsSuspend(baseCurrency: String): List<DatabaseXERow>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(vararg databaseXERows: DatabaseXERow)

    // TODO: Non Internet version of app? Future.
//     REPOPULATION HOLY F YES https://developer.android.com/training/data-storage/room/prepopulate#from-asset

}