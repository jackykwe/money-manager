package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Category

private const val TAG = "cevm"

class CategoryEditViewModel(private val oldCategory: Category) : ViewModel() {

    init {
        if (oldCategory.name == "Add...") {
            oldCategory.apply {
                name = ""
                iconHex = "F02D6"
            }
        }
    }

    private val userRepository = UserRepository.getInstance()
    private val otherCategories = when (val type = oldCategory.type) { // TODO: ASYNC
        "Income" -> ArrayList(userRepository.incomeCategories.value!!).map { it.name }. filter { it != oldCategory.name }  // TODO: DO DIRECT DB QUERY (NON LIVE DATA; ASYNC)
        "Expenses" -> ArrayList(userRepository.expensesCategories.value!!).map { it.name }. filter { it != oldCategory.name }
        else -> throw IllegalArgumentException("Unknown type $type")
    }

    private var _currentCategory = MutableLiveData2(oldCategory.copy())
    val currentCategory: LiveData<Category>
        get() = _currentCategory
    fun changesWereMade(): Boolean {
        return oldCategory != _currentCategory.value
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Submit Checks
     */
    ////////////////////////////////////////////////////////////////////////////////

    private enum class SubmitReadyState {
        NOT_READY_NAME_EMPTY,
        NOT_READY_NAME_INVALID,
        NOT_READY_NAME_DUPLICATE,
        NOT_READY_ICON_HEX_INVALID,
        READY
    }

    val categoryNameETText = MutableLiveData<String>(_currentCategory.value.name)  // Two-way binding; cannot use MutableLiveData2
    private val categoryNameIsNullOrBlank = Transformations.map(categoryNameETText) {
        _currentCategory.value = _currentCategory.value.copy(name = it.trim())
        it.isNullOrBlank()
    }
    private val categoryNameIsInvalid = Transformations.map(_currentCategory) {
        it.name == "Add..."
    }
    private val categoryNameIsDuplicate = Transformations.map(_currentCategory) {
        otherCategories.contains(it.name)
    }

    val iconHexETText = MutableLiveData<String>(_currentCategory.value.iconHex)  // Two-way binding; cannot use MutableLiveData2
    private val iconHexIsInvalid = Transformations.map(iconHexETText) {
        return@map when {
            it.isNullOrBlank() -> {
                iconHexETText.value = "F"
                _currentCategory.value = _currentCategory.value.copy(iconHex = "F0101")
                true
            }
            IconHandler.iconHexIsValid(it) && it != "F02D6" -> {
                _currentCategory.value = _currentCategory.value.copy(iconHex = it)
                false
            }
            else -> {
                _currentCategory.value = _currentCategory.value.copy(iconHex = "F0202")
                true
            }
        }
    }

    private val _submitReady = MediatorLiveData<SubmitReadyState>().apply {
        addSource(categoryNameIsNullOrBlank) { value = updateSubmitReady() }
        addSource(categoryNameIsInvalid) { value = updateSubmitReady() }
        addSource(categoryNameIsDuplicate) { value = updateSubmitReady() }
        addSource(iconHexIsInvalid) { value = updateSubmitReady() }
    }

    private fun updateSubmitReady(): SubmitReadyState {
        return when {
            categoryNameIsNullOrBlank.value ?: true -> SubmitReadyState.NOT_READY_NAME_EMPTY
            categoryNameIsInvalid.value ?: true -> SubmitReadyState.NOT_READY_NAME_INVALID
            categoryNameIsDuplicate.value ?: true -> SubmitReadyState.NOT_READY_NAME_DUPLICATE
            iconHexIsInvalid.value ?: true -> SubmitReadyState.NOT_READY_ICON_HEX_INVALID
            else -> SubmitReadyState.READY
        }
    }

    fun saveBTClicked() {
        when (_submitReady.value) {
            SubmitReadyState.NOT_READY_NAME_EMPTY -> _showSnackBarText.value = "Category Name must not be empty"
            SubmitReadyState.NOT_READY_NAME_INVALID -> _showSnackBarText.value = "Category Name must not be \"Add...\""
            SubmitReadyState.NOT_READY_NAME_DUPLICATE -> _showSnackBarText.value = "This Category Name already exists"
            SubmitReadyState.NOT_READY_ICON_HEX_INVALID -> _showSnackBarText.value = "Icon ID is invalid"
            SubmitReadyState.READY -> TODO("HANDLE SAVE")
        }
    }

    private val _showSnackBarText = MutableLiveData2<String?>(null)
    val showSnackBarText : LiveData<String?>
        get() = _showSnackBarText
    fun snackBarShown() {
        _showSnackBarText.value = null
    }

//
//    fun addCategory(transaction: Transaction) {
//        Log.d(TAG, "addCategory: called")
//        viewModelScope.launch {
//            userRepository.addCategory(transaction)
//        }
//    }





//    private val liveDataActivator = Observer<Any> { }

//    init {
//        memoIsNullOrBlank.observeForever(liveDataActivator)
//    }
//
//    override fun onCleared() {
//        memoIsNullOrBlank.removeObserver(liveDataActivator)
//        super.onCleared()
//    }

}