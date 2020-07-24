package com.kaeonx.moneymanager.xerepository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kaeonx.moneymanager.xerepository.database.XEDatabase
import com.kaeonx.moneymanager.xerepository.database.toDomain
import com.kaeonx.moneymanager.xerepository.domain.XERow
import com.kaeonx.moneymanager.xerepository.network.XENetwork
import com.kaeonx.moneymanager.xerepository.network.toDatabase
import kotlinx.coroutines.Dispatchers
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
class XERepository(application: Application) {

    private val database = XEDatabase.getInstance(application)

    // Load from cache
    // A LiveData anyone can use to observe XERows from the repository
    // LiveData is smart and only runs queries when someone is observing it
    private val _xeRows = database.xeDatabaseDao.getAllXERows()
    val xeRows: LiveData<List<XERow>> = Transformations.map(_xeRows) { it.toDomain() }

    // Refresh cache
    // Won't return anything, so it won't be accidentally used to fetch data
    suspend fun refreshRatesTable(baseCurrency: String = "SGD") {
        // Disc IO is much slower than RAM IO
        // Low level APIs (for read, write, etc.) that the Database uses are blocking,
        // so we have to treat Disc IO separately when using Coroutines.
        // Hence withContext(Dispatchers.IO) to force Kotlin to switch to a IO thread
        // from the relevant thread pool optimised for IO operations.
        withContext(Dispatchers.IO) {
            // TODO: Error Handling
            Log.d("ntwksvc", "WARNING: NETWORK CALL HAPPENING RIGHT NOW")
            val networkXEContainer = XENetwork.retrofitService.fetchNetworkXEContainer(baseCurrency)
            Log.d("ntwksvc", "WARNING: NETWORK CALL DONE")
            Log.d("ntwksvc", "WARNING: UPSERT CALL HAPPENING RIGHT NOW")
            database.xeDatabaseDao.upsertAll(*networkXEContainer.toDatabase())
            Log.d("ntwksvc", "WARNING: UPSERT DONE")
        }
    }

}