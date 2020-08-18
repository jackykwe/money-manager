package com.kaeonx.moneymanager.xerepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.xerepository.database.XEDatabase
import com.kaeonx.moneymanager.xerepository.database.toDomain
import com.kaeonx.moneymanager.xerepository.domain.XERow
import com.kaeonx.moneymanager.xerepository.network.XENetwork
import com.kaeonx.moneymanager.xerepository.network.toDatabase
import kotlinx.coroutines.*

private const val TAG = "xeRepository"

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
    private val userRepository = UserRepository.getInstance()

    // To know which currency to load from the XEDatabase, we must access ccc_home_currency.
    // This value will not be stale, because userRepository.preferences is always fresh
    // (due to observe forever).
//    private val _homeCurrency = MutableLiveData2(UserPDS.getString("ccc_home_currency"))
    private val _homeCurrency = Transformations.map(userRepository.preferences) {
        val returnable =
            it["ccc_home_currency"] as String? ?: UserPDS.getDefaultString("ccc_home_currency")
        checkAndUpdateIfNecessary(returnable)
        returnable
    }

    // Load from cache
    // A LiveData anyone can use to observe XERows from the repository
    // LiveData is smart and only runs queries when someone is observing it (in the active state)
    // I.E. _xeRows is non-stale (because _homeCurrency is non-stale).
//    private val _xeRows = database.xeDatabaseDao.getAllXERows()
    private val _xeRows = Transformations.switchMap(_homeCurrency) { homeCurrency ->
        database.xeDatabaseDao.getXERows(homeCurrency)
    }
    val xeRows: LiveData<List<XERow>> = Transformations.map(_xeRows) {
        Log.d(TAG, "xeRows has size ${_xeRows.value!!.size}")
        it.toDomain()
    }

    // 1. On App Start
    // 2. On Preferences Change
    // Both of the above is factored in in the transformation for _homeCurrency above
    // This is called every time a preference is changed. (Hm. TODO Future problem to worry about)
    private fun checkAndUpdateIfNecessary(baseCurrency: String) {
        CoroutineScope(Dispatchers.Default).launch {
            Log.d(TAG, "checkAndUpdateIfNecessary(): called")
            val xeRowsResult = withContext(Dispatchers.IO) {
                database.xeDatabaseDao.getXERowsSuspend(baseCurrency)
            }
            if (xeRowsResult.isEmpty() ||
                (UserPDS.getBoolean("ccv_enable_online") &&
                        System.currentTimeMillis() -
                        xeRowsResult[0].updateMillis >
                        UserPDS.getString("ccv_online_update_ttl").toLong()
                        )
            ) {
                Log.d(TAG, " checkAndUpdateIfNecessary(): updating")
                withContext(Dispatchers.IO) {
                    refreshRatesTable(baseCurrency)
                }
            } else {
                Log.d(TAG, " checkAndUpdateIfNecessary(): no need to update")
            }
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
    private suspend fun refreshRatesTable(baseCurrency: String) {
        // Disc IO is much slower than RAM IO
        // Low level APIs (for read, write, etc.) that the Database uses are blocking,
        // so we have to treat Disc IO separately when using Coroutines.
        // Hence withContext(Dispatchers.IO) to force Kotlin to switch to a IO thread
        // from the relevant thread pool optimised for IO operations.
        withContext(Dispatchers.IO) {
            Log.d("ntwksvc", "WARNING: NETWORK CALL HAPPENING RIGHT NOW")
            try {
                val networkXEContainer =
                    XENetwork.retrofitService.fetchNetworkXEContainer(baseCurrency)
                Log.d("ntwksvc", "WARNING: NETWORK CALL DONE")
                ensureActive()
                Log.d("ntwksvc", "WARNING: UPSERT CALL HAPPENING RIGHT NOW")
                database.xeDatabaseDao.upsertAll(*networkXEContainer.toDatabase(System.currentTimeMillis()))
                Log.d("ntwksvc", "WARNING: UPSERT DONE")
            } catch (e: Exception) {
                Log.d(
                    "ntwksvc",
                    "WARNING: NETWORK CALL FAILED due to CAUSE ${e.cause}, CLASS ${e.javaClass}, MESSAGE ${e.message}"
                )
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
                    Log.d(
                        TAG,
                        "WARN: OPENING INSTANCE TO XE REPOSITORY FOR ${Firebase.auth.currentUser!!.uid}"
                    )
                    // Opening a connection to a database is expensive!
                    instance = XERepository()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // Used when logging out
        fun dropInstance() {
            Log.d(TAG, "WARN: XE REPOSITORY INSTANCE DROPPED")
            INSTANCE?.clearPermanentObservers()
            INSTANCE = null
        }
    }
}