package com.kaeonx.moneymanager.xerepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.xerepository.database.XEDatabase
import com.kaeonx.moneymanager.xerepository.database.toDomain
import com.kaeonx.moneymanager.xerepository.domain.XERow
import com.kaeonx.moneymanager.xerepository.network.XENetwork
import com.kaeonx.moneymanager.xerepository.network.toDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * The repository will provide a unified view of our data from several sources.
 * In our app, data will come fom the network and the database to be combined in the repository.
 * Users of the repository don't have to care where the data comes from - it can come fomr th e network, the database, or some other source we'll add later.
 * And it's all hidden away behind the interface provided by the repository.
 * The repository also manages the data in the cache. The database doesn't have any logic for handling the offline cache. It just has a method to add new values
 * and another one to get all values. The repository will have all the logic to take a network result and update the database to make sure it's up to date. And
 */
// We need a database, so we'll ask users to pass in the XEDatabase object.
// Doing so, we don't need to keep a reference to the Android context in our repository,
// which can cause leaks.
// Actually I feel like passing in the application here would be better:
// then the view model won't need to know about the database in the first place.
//class XERepository(private val database: XEDatabase) {
class XERepository private constructor() {

    private val database = XEDatabase.getInstance()

    // Load from cache
    // A LiveData anyone can use to observe XERows from the repository
    // LiveData is smart and only runs queries when someone is observing it (in the active state)
    // I.E. _xeRows is non-stale (because _homeCurrency is non-stale).
//    private val _xeRows = database.xeDatabaseDao.getAllXERows()
    val xeRows: LiveData<List<XERow>> = Transformations.map(database.xeDatabaseDao.getAllXERows()) {
        it.toDomain()
    }

    internal suspend fun checkAndUpdateIfNecessary() {
        withContext(Dispatchers.IO) {
            val xeRowsResult = database.xeDatabaseDao.getAllXERowsSuspend()
            if (xeRowsResult.isEmpty() ||
                (UserPDS.getBoolean("ccv_enable_online") &&
                        System.currentTimeMillis() -
                        xeRowsResult[0].updateMillis >
                        UserPDS.getString("ccv_online_update_ttl").toLong()
                        )
            ) refreshRatesTable()
        }
    }

    private val liveDataActivator = Observer<Any> { }

    init {
        xeRows.observeForever(liveDataActivator)
    }

    private fun clearPermanentObservers() {
        xeRows.removeObserver(liveDataActivator)
    }

////////////////////////////////////////////////////////////////////////////////
    /**
     * Network
     */
////////////////////////////////////////////////////////////////////////////////

// Refresh cache
// Won't return anything, so it won't be accidentally used to fetch data
    private suspend fun refreshRatesTable() {
        // Disc IO is much slower than RAM IO
        // Low level APIs (for read, write, etc.) that the Database uses are blocking,
        // so we have to treat Disc IO separately when using Coroutines.
        // Hence withContext(Dispatchers.IO) to force Kotlin to switch to a IO thread
        // from the relevant thread pool optimised for IO operations.
        withContext(Dispatchers.IO) {
            try {
                val networkXEContainer = XENetwork.retrofitService.fetchNetworkXEContainer("SGD")
                ensureActive()
                database.xeDatabaseDao.upsertAll(*networkXEContainer.toDatabase(System.currentTimeMillis()))
            } catch (e: Exception) {
                Unit
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: XERepository? = null

        fun getInstance(): XERepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    // Opening a connection to a database is expensive!
                    instance = XERepository()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // Used when logging out
        fun dropInstance() {
            INSTANCE?.clearPermanentObservers()
            INSTANCE = null
        }
    }
}