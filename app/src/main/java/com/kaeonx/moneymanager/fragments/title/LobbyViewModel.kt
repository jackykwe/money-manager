package com.kaeonx.moneymanager.fragments.title

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.activities.MainActivityViewModel
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.*
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File

class LobbyViewModel : ViewModel() {

    internal fun initialise() {
        viewModelScope.launch {
            val xeRepository = XERepository.getInstance()
            val userRepository = UserRepository.getInstance()
            while (
                userRepository.accounts.value == null ||
                userRepository.categories.value == null ||
                userRepository.preferences.value == null ||
                xeRepository.xeRows.value == null
            ) {
                ensureActive()
                delay(1L)
            }

            UserPDS.putDSPString("logged_in_uid", Firebase.auth.currentUser!!.uid)

            val downloadedFile = File(
                MainActivityViewModel.buildDownloadedDBFilePath(
                    Firebase.auth.currentUser!!.uid
                )
            )
            if (downloadedFile.exists()) {
                try {
                    ensureActive()
                    val jsonObject = JSONObject(
                        IEFileHandler.readGZToRoot(downloadedFile)
                    )


                    // Database version
                    val version = jsonObject.optInt("db")
                    if (version == 0) {
                        throw IllegalStateException("JSON missing valid \"db\"")
                    } else {
                        val dbVersion =
                            UserDatabase.getInstance().openHelper.readableDatabase.version
                        if (version > dbVersion) throw IllegalStateException("\"db\" is too high")
                    }


                    // Transactions
                    val transactions = IETransactionsHandler.jsonArrayToList(
                        jsonObject.optJSONArray("transactions")
                            ?: throw IllegalStateException("JSON missing \"transactions\"")
                    ).map {
                        ensureActive()
                        it.importEnsureValid()
                        it
                    }
                    ensureActive()
                    // Primary key check
                    if (transactions.map { it.transactionId }.toSet().size != transactions.size)
                        throw IllegalStateException("duplicate ids found among Transactions")


                    // Budget
                    val budgets = IEBudgetsHandler.jsonArrayToList(
                        jsonObject.optJSONArray("budgets")
                            ?: throw IllegalStateException("JSON missing \"budgets\"")
                    ).map {
                        ensureActive()
                        it.importEnsureValid()
                        it
                    }
                    ensureActive()
                    // Primary key check
                    if (budgets.map { it.category }.toSet().size != budgets.size)
                        throw IllegalStateException("duplicate categories found among Budgets")


                    // Debts (TODO Future)


                    // Categories
                    val categories = IECategoriesHandler.jsonArrayToList(
                        jsonObject.optJSONArray("categories")
                            ?: throw IllegalStateException("JSON missing \"categories\"")
                    ).map {
                        ensureActive()
                        it.importEnsureValid()
                        it
                    }
                    ensureActive()
                    // Primary key check
                    if (categories.map { it.categoryId }.toSet().size != categories.size)
                        throw IllegalStateException("duplicate ids found among Categories")
                    ensureActive()
                    if (categories.map { it.name }.toSet().size != categories.size)
                        throw IllegalStateException("duplicate names found among Categories")
                    ensureActive()
                    if (categories.map { it.name }.toSet().size != categories.size)
                        throw IllegalStateException("duplicate names found among Categories")


                    // Accounts
                    val accounts = IEAccountsHandler.jsonArrayToList(
                        jsonObject.optJSONArray("accounts")
                            ?: throw IllegalStateException("JSON missing \"accounts\"")
                    ).map {
                        ensureActive()
                        it.importEnsureValid()
                        it
                    }
                    ensureActive()
                    // Primary key check
                    if (accounts.map { it.accountId }.toSet().size != accounts.size)
                        throw IllegalStateException("duplicate ids found among Accounts")
                    ensureActive()
                    val uniqueAccounts = accounts.map { it.name }.toSet()
                    if (uniqueAccounts.size != accounts.size)
                        throw IllegalStateException("duplicate names found among Accounts")


                    // Settings
                    val validKeys = UserPDS.getAllValidKeys()
                    val preferences = IEPreferencesHandler.jsonArrayToList(
                        jsonObject.optJSONArray("settings")
                            ?: throw IllegalStateException("JSON missing \"settings\"")
                    )
                    preferences.forEach {
                        ensureActive()
                        if (it.key !in validKeys) throw IllegalStateException("unknown Setting: \"${it.key}\"")
                        if (it.valueText == null && it.valueInteger == null)
                            throw IllegalStateException("Setting without any value: \"${it.key}\"")
                        if (it.valueText != null && it.valueInteger != null)
                            throw IllegalStateException("Setting with two values: \"${it.key}\"")
                        it.valueText?.let { valueText ->
                            when (it.key) {
                                // Handle any keys not controlled by string arrays here
                                "tst_default_account" -> {
                                    if (valueText !in uniqueAccounts)
                                        throw IllegalStateException("invalid \"${it.key}\" value\nThis account does not exist.")
                                }
                                else -> if (valueText !in IEPreferencesHandler.getValuesStringArrayOf(
                                        it.key
                                    )
                                )
                                    throw IllegalStateException("invalid \"${it.key}\" value")
                            }
                        }
                        it.valueInteger?.let { valueInteger ->
                            if (valueInteger != 0 && valueInteger != 1)
                                throw IllegalStateException("invalid \"${it.key}\" value")
                        }
                    }


                    UserPDS.getString("dsp_theme")
                    UserPDS.getDSPString("dsp_theme", "light")
                    userRepository.overwriteDatabaseTransactionSuspend(
                        transactionsList = transactions,
                        categoriesList = categories,
                        accountsList = accounts,
                        budgetsList = budgets,
                        preferencesList = preferences
                    )
                    downloadedFile.delete()
                    delay(1000L)  // To allow UserPDS to update.
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _showErrorSnackbar.value =
                            "Cloud data is of improper form. Please report this bug."
                    }
                }
            }

            _attemptToRefreshRatesTable.value = true

            // Ensure theme is correct
            val userTheme = UserPDS.getString("dsp_theme")
            val sharedPrefTheme = UserPDS.getDSPString("dsp_theme", "light")
            if (sharedPrefTheme != userTheme) {
                UserPDS.putDSPString("dsp_theme", UserPDS.getString("dsp_theme"))
                withContext(Dispatchers.Main) {
                    _showApplyingThemeSnackbar.value = true
                }
            } else {
                _initDone.value = true
            }
        }
    }

    private val _showErrorSnackbar = MutableLiveData2<String?>(null)
    internal val showErrorSnackbar: LiveData<String?>
        get() = _showErrorSnackbar

    internal fun showErrorSnackbarHandled() {
        _showErrorSnackbar.value = null
    }

    private val _showApplyingThemeSnackbar = MutableLiveData2(false)
    internal val showApplyingThemeSnackbar: LiveData<Boolean>
        get() = _showApplyingThemeSnackbar

    internal fun showApplyingThemeSnackbarHandled() {
        _showApplyingThemeSnackbar.value = false
    }

    private val _initDone = MutableLiveData2(false)
    internal val initDone: LiveData<Boolean>
        get() = _initDone

    internal fun initDoneHandled() {
        _initDone.value = false
    }

    private val _attemptToRefreshRatesTable = MutableLiveData2(false)
    internal val attemptToRefreshRatesTable: LiveData<Boolean>
        get() = _attemptToRefreshRatesTable

    internal fun attemptToRefreshRatesTableHandled() {
        _attemptToRefreshRatesTable.value = false
    }

}