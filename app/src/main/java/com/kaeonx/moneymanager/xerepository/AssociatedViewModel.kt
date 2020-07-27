package com.kaeonx.moneymanager.xerepository

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val TAG = "placeholderViewModel"

/**
 * This ViewModel is designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during activity
 * or fragment lifecycle events.
 */
class FirstFragmentViewModel(application: Application) : AndroidViewModel(application) {
//    enum class NetworkFetchStatus {
//        LOADING,
//        ERROR,
//        DONE
//    }

//    private val _status = MutableLiveData2<NetworkFetchStatus>()
//    val status: LiveData<NetworkFetchStatus>
//        get() = _status

    //    private val database = XEDatabase.getXEDatabaseInstance(application)
//    private val xeRepository = XERepository(database)
    private val xeRepository = XERepository()

    init {
        viewModelScope.launch {
            try {
                xeRepository.refreshRatesTable("SGD")
            } catch (t: Throwable) {
                Log.d(TAG, "init: refreshRatesTable failed because ${t.message}")
            }
        }
    }

    private val xeRows = xeRepository.xeRows
    val xeRowsString: LiveData<String> = Transformations.map(xeRows) {
        xeRows.value.toString()
    }
}