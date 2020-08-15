package com.kaeonx.moneymanager.fragments.importexport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentImportExportBinding
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.*
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.OUTPUT_TO_FILE
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.READ_FROM_FILE
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

private const val TAG = "inexfrag"

class ImportExportFragment : Fragment() {

    private lateinit var binding: FragmentImportExportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentImportExportBinding.inflate(inflater, container, false)
        binding.importBT.setOnClickListener {
            startActivityForResult(
                IEFileHandler.constructReadFileIntent(),
                READ_FROM_FILE
            )
        }
        binding.exportBT.setOnClickListener {
            val yMdHMs =
                CalendarHandler.getFormattedString(System.currentTimeMillis(), "yyMMdd_HHmmss")
            startActivityForResult(
                IEFileHandler.constructWriteFileIntent("${yMdHMs}_backup.json"),
                OUTPUT_TO_FILE
            )
        }
        return binding.root
    }

    private fun startUI() {
        // TODO: LOCK UP/BACK
        // BTs
        binding.importBT.isEnabled = false
        binding.exportBT.isEnabled = false

        // resultIV and progressPI
        binding.resultIV.animate()
            .alpha(0f)
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setListener(null)
        binding.progressPI.progress = 0
        binding.progressPI.animate()
            .alpha(1f)
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setListener(null)

        // TVs
        binding.warningTV.text = getString(R.string.import_export_loading_text)
        binding.warningTV.animate()
            .alpha(1f)
            .setDuration(
                resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            )
            .setListener(null)
    }

    private var previousNewProgressText: String? = null

    private suspend fun updateUI(newProgressText: String, newProgress: Int) {
        if (newProgressText.takeLast(1) != "…") throw IllegalArgumentException("newProgressText must end with …")
        previousNewProgressText = newProgressText
        withContext(Dispatchers.Main) {
            binding.progressPI.setProgress(newProgress, true)
            binding.progressTV.text = newProgressText
        }
    }

    private fun doneUI(error: Boolean, mode: Int, exceptionText: String?) {
        val modeString = when (mode) {
            READ_FROM_FILE -> "import"
            OUTPUT_TO_FILE -> "export"
            else -> throw IllegalArgumentException("Unknown doneUI mode: $mode")
        }
        binding.importBT.isEnabled = true
        binding.exportBT.isEnabled = true

        // resultIV and progressPI
        binding.progressPI.animate()
            .alpha(0f)
            .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
            .setListener(null)
        binding.resultIV.setImageDrawable(
            if (error) {
                ContextCompat.getDrawable(requireContext(), R.drawable.mdi_alert_circle_amber)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.mdi_check_circle_green)
            }
        )
        binding.resultIV.animate()
            .alpha(1f)
            .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
            .setListener(null)

        // TVs
        binding.progressTV.text = if (!error) {
            "Successfully ${modeString}ed data"
        } else {
            if (previousNewProgressText == null) {
                "Unable to $modeString data"
            } else {
                "${previousNewProgressText!!.takeLast(previousNewProgressText!!.length - 1)} failed"
            }
        }
        if (error) {
            binding.warningTV.text = exceptionText.toString()
        } else {
            binding.warningTV.animate()
                .alpha(0f)
                .setDuration(
                    resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                )
                .setListener(null)
        }
    }

    /**
     * Number of yielded values is `intervalCount + 1`.
     */
    private fun generatePercentIterator(intervalCount: Int): Iterator<Int> = sequence {
        var iteration = 0f
        while (iteration <= intervalCount) {
            yield((iteration++ / intervalCount * 100).toInt())
        }
    }.iterator()

    private fun importData(data: Intent?) {
        // TODO: WARN THAT THIS WILL REPLACE EVERYTHING PROCEED Y/N
        startUI()
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val progressIterator = generatePercentIterator(8)
                updateUI("Reading from JSON…", progressIterator.next())
                val readData = withContext(Dispatchers.IO) {
                    IEFileHandler.readJSONString(requireContext().contentResolver, data)
                }
                ensureActive()
                val jsonObject = JSONObject(readData)


                // Transactions
                updateUI("Validating JSON Transactions…", progressIterator.next())
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


                // Categories
                updateUI("Validating JSON Categories…", progressIterator.next())
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
                val uniqueCategories = categories.map { it.name }.toSet()
                if (uniqueCategories.size != categories.size)
                    throw IllegalStateException("duplicate names found among Categories")


                // Budget
                updateUI("Validating JSON Budgets…", progressIterator.next())
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
                val permittedCategories = uniqueCategories + listOf("Overall")
                budgets.forEach {
                    ensureActive()
                    if (it.category !in permittedCategories)
                        throw IllegalStateException("Budget defined for non-existent Category: \"${it.category}\"")
                }

                // Debts (TODO)
                updateUI("Validating JSON Debts…", progressIterator.next())


                // Accounts
                updateUI("Validating JSON Accounts…", progressIterator.next())
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
                if (accounts.map { it.name }.toSet().size != accounts.size)
                    throw IllegalStateException("duplicate names found among Accounts")


                // Settings
                val validKeys = UserPDS.getAllValidKeys()
                updateUI("Validating JSON Settings…", progressIterator.next())
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
                }

                updateUI("Overwriting Transactions…", progressIterator.next())
                val userRepository = UserRepository.getInstance()
                userRepository.overwriteDatabase(
                    transactionsList = transactions,
                    categoriesList = categories,
                    accountsList = accounts,
                    budgetsList = budgets,
                    preferencesList = preferences
                )

                updateUI("…", progressIterator.next())
                withContext(Dispatchers.Main) { doneUI(false, READ_FROM_FILE, null) }
            } catch (e: Exception) {
                Log.e(TAG, e.stackTrace.joinToString("\n"))
                withContext(Dispatchers.Main) {
                    doneUI(
                        true,
                        READ_FROM_FILE,
                        when (e) {
                            is IOException -> "unspecified IO error\nPlease report this bug."
                            is JSONException -> "malformed JSON file\nCheck that you selected a JSON file\nand your JSON is formatted properly."
                            is IllegalStateException -> e.message
                                ?: "Unqualified ISE\nPlease report this bug."
                            else -> "unspecified unknown error\nPlease report this bug.".also {
                                Log.e(
                                    TAG,
                                    "unspecified unknown error of class ${e.javaClass}, cause ${e.cause}, message ${e.message}"
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun exportData(data: Intent?) {
        startUI()
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val output = JSONObject()
                val repository = UserRepository.getInstance()
                val progressIterator = generatePercentIterator(7)

                // Transactions
                updateUI("Exporting Transactions…", progressIterator.next())
                output.put(
                    "transactions",
                    IETransactionsHandler.listToJsonArray(
                        repository.exportTransactionsSuspend()
                    )
                )

                // Budget
                updateUI("Exporting Budgets…", progressIterator.next())
                output.put(
                    "budgets",
                    IEBudgetsHandler.listToJsonArray(
                        repository.exportBudgetsSuspend()
                    )
                )

                // Debts (TODO)
                updateUI("Exporting Debts…", progressIterator.next())

                // Categories
                updateUI("Exporting Categories…", progressIterator.next())
                output.put(
                    "categories",
                    IECategoriesHandler.listToJsonArray(
                        repository.exportCategoriesSuspend()
                    )
                )

                // Accounts
                updateUI("Exporting Accounts…", progressIterator.next())
                output.put(
                    "accounts",
                    IEAccountsHandler.listToJsonArray(
                        repository.exportAccountsSuspend()
                    )
                )

                // Settings
                updateUI("Exporting Settings…", progressIterator.next())
                output.put(
                    "settings",
                    IEPreferencesHandler.listToJsonArray(
                        repository.exportPreferencesSuspend()
                    )
                )

                updateUI("Writing to JSON…", progressIterator.next())
                withContext(Dispatchers.IO) {
                    IEFileHandler.writeJSONString(
                        contentResolver = requireContext().contentResolver,
                        data = data,
                        contentToWrite = output.toString(2)  // balance between readability and space
                    )
                }

                updateUI("…", progressIterator.next())
                withContext(Dispatchers.Main) { doneUI(false, OUTPUT_TO_FILE, null) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    doneUI(
                        true,
                        OUTPUT_TO_FILE,
                        "Unknown ${e.javaClass.toString().substring(6)}"
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OUTPUT_TO_FILE && resultCode == Activity.RESULT_OK) {
            exportData(data)
        } else if (requestCode == READ_FROM_FILE && resultCode == Activity.RESULT_OK) {
            importData(data)
        }
    }
}