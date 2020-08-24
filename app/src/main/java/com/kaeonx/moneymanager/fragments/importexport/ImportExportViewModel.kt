package com.kaeonx.moneymanager.fragments.importexport

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.*
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ImportExportViewModel : ViewModel() {

    private var previousNewProgressText: String? = null

    /**
     * Number of yielded values is `intervalCount + 1`.
     * @param intervalCount The number of times this.next() is called
     * (i.e. the number of actual steps you need to perform).
     */
    private fun generatePercentIterator(intervalCount: Int): Iterator<Int> = sequence {
        var iteration = 0f
        while (iteration < intervalCount) {
            yield((iteration++ / intervalCount * 100).toInt())
        }
    }.iterator()

    private fun generateFailureProgressTVText(): String =
        "${previousNewProgressText!!.take(previousNewProgressText!!.length - 1)} failed"

    private fun declareUpdateUIDone() {
        _updateUI.value = Pair("…", 100)
    }

    internal fun importData(data: Intent?) {
        _startUI.value = true
        viewModelScope.launch {
            try {
                val progressIterator = generatePercentIterator(9)

                "Reading from JSON…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
                val readData = withContext(Dispatchers.IO) {
                    IEFileHandler.readJSONString(App.context.contentResolver, data)
                }
                ensureActive()
                val jsonObject = JSONObject(readData)


                // Database version
                "Validating JSON DB Version…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
                val version = jsonObject.optInt("db")
                if (version == 0) {
                    throw IllegalStateException("JSON missing valid \"db\"")
                } else {
                    val dbVersion = UserDatabase.getInstance().openHelper.readableDatabase.version
                    if (version > dbVersion) throw IllegalStateException("\"db\" is too high")
                }
                // TODO: FIREBASE RULES (STORAGE, AUTH)


                // Transactions
                "Validating JSON Transactions…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
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
                "Validating JSON Budgets…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
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
                "Validating JSON Debts…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }


                // Categories
                "Validating JSON Categories…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
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
                "Validating JSON Accounts…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
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
                "Validating JSON Settings…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
                val validKeys = UserPDS.getAllValidKeys()
                val preferences = IEPreferencesHandler.jsonArrayToList(
                    jsonObject.optJSONArray("settings")
                        ?: throw IllegalStateException("JSON missing \"settings\"")
                )
                preferences.forEach {
                    ensureActive()
                    if (it.key !in validKeys) throw IllegalStateException("unknown Setting: \"${it.key}\"")
                    if (it.valueText == null && it.valueInteger == null)
                        throw IllegalStateException("setting without any value: \"${it.key}\"")
                    if (it.valueText != null && it.valueInteger != null)
                        throw IllegalStateException("setting with two values: \"${it.key}\"")
                    it.valueText?.let { valueText ->
                        when (it.key) {
                            // Handle any keys not controlled by string arrays here
                            "tst_default_account" -> {
                                if (valueText !in uniqueAccounts)
                                    throw IllegalStateException("invalid \"${it.key}\" value\nThis account does not exist.")
                            }
                            else -> if (valueText !in IEPreferencesHandler.getValuesStringArrayOf(it.key))
                                throw IllegalStateException("invalid \"${it.key}\" value")
                        }
                    }
                    it.valueInteger?.let { valueInteger ->
                        if (valueInteger != 0 && valueInteger != 1)
                            throw IllegalStateException("invalid \"${it.key}\" value")
                    }
                }

                "Overwriting Data…".let {
                    withContext(Dispatchers.Main) {
                        _updateUI.value = Pair(it, progressIterator.next())
                    }
                    previousNewProgressText = it
                }
                UserPDS.getString("dsp_theme")
                UserPDS.getDSPString("dsp_theme", "light")
                val userRepository = UserRepository.getInstance()
                userRepository.overwriteDatabaseTransactionSuspend(
                    transactionsList = transactions,
                    categoriesList = categories,
                    accountsList = accounts,
                    budgetsList = budgets,
                    preferencesList = preferences
                )
                delay(1000L)  // To allow UserPDS to update.

                withContext(Dispatchers.Main) {
                    declareUpdateUIDone()
                    _doneUI.value = DoneUIData(
                        resultIVDrawableId = R.drawable.mdi_check_circle_green,
                        progressTVText = "Successfully imported data",
                        exceptionText = null
                    )

                    // Ensure theme is correct
                    val userTheme = UserPDS.getString("dsp_theme")
                    val sharedPrefTheme = UserPDS.getDSPString("dsp_theme", "light")
                    if (sharedPrefTheme != userTheme) {
                        UserPDS.putDSPString("dsp_theme", UserPDS.getString("dsp_theme"))
                        withContext(Dispatchers.Main) {
                            _showApplyingThemeSnackbar.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _doneUI.value = DoneUIData(
                        resultIVDrawableId = R.drawable.mdi_cloud_alert_amber,
                        progressTVText = generateFailureProgressTVText(),
                        exceptionText = when (e) {
                            is IOException -> "unspecified IO error\nPlease report this bug."
                            is JSONException -> "malformed JSON file\nCheck that you selected a JSON file\nand your JSON is formatted properly."
                            is IllegalStateException -> e.message
                                ?: "Unqualified ISE\nPlease report this bug."
                            else -> "unspecified unknown error\nPlease report this bug."
                        }
                    )
                }
            }
        }
    }

    internal fun exportData(data: Intent?) {
        _startUI.value = true
        viewModelScope.launch {
            val output = JSONObject()
            val repository = UserRepository.getInstance()
            val progressIterator = generatePercentIterator(8)


            // Database version (for future migrations, if needed)
            ensureActive()
            "Exporting DB Version…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            val version = UserDatabase.getInstance().openHelper.readableDatabase.version
            output.put("db", version)


            // Transactions
            ensureActive()
            "Exporting Transactions…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "transactions",
                IETransactionsHandler.listToJsonArray(
                    repository.exportTransactionsSuspend()
                )
            )

            // Budget
            ensureActive()
            "Exporting Budgets…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "budgets",
                IEBudgetsHandler.listToJsonArray(
                    repository.exportBudgetsSuspend()
                )
            )

            // Debts (TODO Future)
            ensureActive()
            "Exporting Debts…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }

            // Categories
            ensureActive()
            "Exporting Categories…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "categories",
                IECategoriesHandler.listToJsonArray(
                    repository.exportCategoriesSuspend()
                )
            )

            // Accounts
            ensureActive()
            "Exporting Accounts…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "accounts",
                IEAccountsHandler.listToJsonArray(
                    repository.exportAccountsSuspend()
                )
            )

            // Settings
            ensureActive()
            "Exporting Settings…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            output.put(
                "settings",
                IEPreferencesHandler.listToJsonArray(
                    repository.exportPreferencesSuspend()
                )
            )

            ensureActive()
            "Writing to JSON…".let {
                withContext(Dispatchers.Main) {
                    _updateUI.value = Pair(it, progressIterator.next())
                }
                previousNewProgressText = it
            }
            withContext(Dispatchers.IO) {
                IEFileHandler.writeJSONString(
                    contentResolver = App.context.contentResolver,
                    data = data,
                    contentToWrite = output.toString(2)  // balance between readability and space
                )
            }

            withContext(Dispatchers.Main) {
                declareUpdateUIDone()
                _doneUI.value = DoneUIData(
                    resultIVDrawableId = R.drawable.mdi_cloud_check_green,
                    progressTVText = "Successfully exported data",
                    exceptionText = null
                )
            }
        }
    }

    private val _startUI = MutableLiveData2(false)
    internal val startUI: LiveData<Boolean>
        get() = _startUI

    internal fun startUIHandled() {
        _startUI.value = false
    }

    private val _updateUI = MutableLiveData2<Pair<String, Int>?>(null)
    internal val updateUI: LiveData<Pair<String, Int>?>
        get() = _updateUI

    internal fun updateUIHandled() {
        _updateUI.value = null
    }

    private val _doneUI = MutableLiveData2<DoneUIData?>(null)
    internal val doneUI: LiveData<DoneUIData?>
        get() = _doneUI

    internal fun doneUIHandled() {
        // Unit so that "Successfully X data" message persists even after activity recreate
    }

    private val _showApplyingThemeSnackbar = MutableLiveData2(false)
    internal val showApplyingThemeSnackbar: LiveData<Boolean>
        get() = _showApplyingThemeSnackbar

    internal fun showApplyingThemeSnackbarHandled() {
        _showApplyingThemeSnackbar.value = false
    }

}