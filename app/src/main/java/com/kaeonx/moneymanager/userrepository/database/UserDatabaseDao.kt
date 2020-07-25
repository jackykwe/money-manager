package com.kaeonx.moneymanager.userrepository.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDatabaseDao {
    /*
    @Query("SELECT * FROM currency_conversion_table WHERE `key` = :key")
    fun getTxnRow(key: Long): DatabaseTxnRow

//    @Delete
//    fun deleteTxnRows(rows: List<TxnRow>): Int

    @Query("SELECT * FROM currency_conversion_table ORDER BY `key` DESC")
    fun getAllTxnRows(): LiveData<List<DatabaseTxnRow>>
    */

    @Insert
    suspend fun insertTxn(databaseTxn: DatabaseTxn)
//
    @Update
    suspend fun updateTxn(databaseTxn: DatabaseTxn)  // Key must be the same

    @Delete
    suspend fun deleteTxn(databaseTxn: DatabaseTxn)

    // TODO: WARN USER IF THEY WILL PERFORM THIS ACTION
    @Query("DELETE FROM txn_table")
    suspend fun clearAllData()

    @Query("SELECT * FROM txn_table")
    fun getAllTxns(): LiveData<List<DatabaseTxn>>

    // TODO: REPOPULATION HOLY F YES https://developer.android.com/training/data-storage/room/prepopulate#from-asset
}