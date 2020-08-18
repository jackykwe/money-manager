package com.kaeonx.moneymanager.fragments.importexport

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentCloudBinding
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.*
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.DELETE_DATA
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.UPLOAD_DATA
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "cloudfrag"

class CloudFragment : Fragment() {

    private lateinit var binding: FragmentCloudBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentCloudBinding.inflate(inflater, container, false)

        val lastUploadTime = UserPDS.getDSPLong(
            "${Firebase.auth.currentUser!!.uid}_last_upload_time",
            -1L
        )
        when (lastUploadTime) {
            -1L -> binding.lastUploadedTV.text =
                if (Firebase.auth.currentUser!!.isAnonymous) {
                    "Cloud Backup unavailable for Guests"
                } else {
                    getString(R.string.no_cloud_data_found)
                }
            else -> {
                val dateFormat = UserPDS.getString("dsp_date_format")
                val timeFormat = UserPDS.getString("dsp_time_format")
                binding.lastUploadedTV.text = CalendarHandler.getFormattedString(
                    lastUploadTime,
                    "'Last uploaded:' $timeFormat 'on' $dateFormat"
                )
                binding.resultIV.alpha = 1f
            }
        }

        binding.uploadBT.apply {
            if (Firebase.auth.currentUser!!.isAnonymous) {
                isEnabled = false
            } else {
                setOnClickListener { uploadData() }
            }
        }
        binding.deleteDataBT.apply {
            if (Firebase.auth.currentUser!!.isAnonymous) {
                isEnabled = false
            } else {
                setOnClickListener { deleteData() }
            }
        }
        return binding.root
    }

    private fun startUI() {
        // TODO: LOCK UP/BACK
        // BTs
        binding.uploadBT.isEnabled = false
        binding.deleteDataBT.isEnabled = false

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
        val modeString by lazy {
            when (mode) {
                UPLOAD_DATA -> "upload"
                DELETE_DATA -> "delete"
                else -> throw IllegalArgumentException("Unknown doneUI mode: $mode")
            }
        }
        val modeStringPast by lazy {
            when (mode) {
                UPLOAD_DATA -> "uploaded"
                DELETE_DATA -> "deleted"
                else -> throw IllegalArgumentException("Unknown doneUI mode: $mode")
            }
        }
        binding.uploadBT.isEnabled = true
        binding.deleteDataBT.isEnabled = true

        // resultIV and progressPI
        binding.progressPI.animate()
            .alpha(0f)
            .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
            .setListener(null)
        binding.resultIV.setImageDrawable(
            if (error) {
                ContextCompat.getDrawable(requireContext(), R.drawable.mdi_cloud_alert_amber)
            } else {
                when (mode) {
                    UPLOAD_DATA -> ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.mdi_cloud_check_green
                    )
                    DELETE_DATA -> ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.mdi_cloud_off_outline_green
                    )
                    else -> throw IllegalArgumentException("Unknown doneUI mode: $mode")
                }
            }
        )
        binding.resultIV.animate()
            .alpha(1f)
            .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
            .setListener(null)

        // TVs
        binding.progressTV.text = if (!error) {
            "Successfully $modeStringPast data"
        } else {
            if (previousNewProgressText == null) {
                "Unable to $modeString data"
            } else {
                "${previousNewProgressText!!.take(previousNewProgressText!!.length - 1)} failed"
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
     * @param intervalCount The number of times this.next() is called -1.
     */
    private fun generatePercentIterator(intervalCount: Int): Iterator<Int> = sequence {
        var iteration = 0f
        while (iteration <= intervalCount) {
            yield((iteration++ / intervalCount * 100).toInt())
        }
    }.iterator()

    private fun uploadData() {
        startUI()
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val output = JSONObject()
                val repository = UserRepository.getInstance()
                val progressIterator = generatePercentIterator(8)

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
                IEFileHandler.saveRootToFile(
                    IEFileHandler.buildUserFilePath(Firebase.auth.currentUser!!.uid),
                    output.toString()
                )

                updateUI("Uploading…", progressIterator.next())
                val uploadTask = AuthViewModel.uploadJSON(Firebase.auth.currentUser!!.uid)
                uploadTask
                    .addOnSuccessListener { taskSnapshot ->
                        UserPDS.putDSPLong(
                            "${Firebase.auth.currentUser!!.uid}_last_upload_time",
                            taskSnapshot.metadata!!.updatedTimeMillis
                        )
                        lifecycleScope.launch {
                            val dateFormat = UserPDS.getString("dsp_date_format")
                            val timeFormat = UserPDS.getString("dsp_time_format")
                            updateUI("…", progressIterator.next())
                            withContext(Dispatchers.Main) {
                                binding.lastUploadedTV.text = CalendarHandler.getFormattedString(
                                    taskSnapshot.metadata!!.updatedTimeMillis,
                                    "'Last uploaded:' $timeFormat 'on' $dateFormat"
                                )
                                doneUI(false, UPLOAD_DATA, null)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            TAG,
                            "uploadTask: failed, " +
                                    "with exception $exception, " +
                                    "errorCode ${(exception as StorageException).errorCode}, " +
                                    "cause ${exception.cause}, " +
                                    "message ${exception.message}, " +
                                    "stacktrace ${exception.stackTrace.joinToString("\n")}"
                        )
                        doneUI(
                            true,
                            UPLOAD_DATA,
                            "upload failed [${exception.errorCode}]\nCheck your internet connection."
                        )
                    }
                    .addOnCanceledListener {
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                doneUI(
                                    true,
                                    UPLOAD_DATA,
                                    "upload cancelled"
                                )
                            }
                        }
                    }
//                    .addOnPausedListener { taskSnapshot -> }
            } catch (e: Exception) {
                doneUI(
                    true,
                    UPLOAD_DATA,
                    "Unknown ${e.javaClass.toString().substring(6)}"
                )
            }
        }
    }

    private fun deleteData() {
        startUI()
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val progressIterator = generatePercentIterator(1)
                updateUI("Deleting cloud data…", progressIterator.next())
                AuthViewModel.deleteJSON(Firebase.auth.currentUser!!.uid)
                    .addOnSuccessListener {
                        UserPDS.removeDSPKey("${Firebase.auth.currentUser!!.uid}_last_upload_time")
                        binding.lastUploadedTV.text = getString(R.string.no_cloud_data_found)
                        doneUI(false, DELETE_DATA, null)
                    }
                    .addOnFailureListener { exception ->
                        when ((exception as StorageException).errorCode) {
                            StorageException.ERROR_OBJECT_NOT_FOUND -> {
                                doneUI(true, DELETE_DATA, "Nothing to delete")
                            }
                            else -> {
                                Log.e(
                                    TAG,
                                    "uploadTask: failed, " +
                                            "with exception $exception, " +
                                            "errorCode ${exception.errorCode}, " +
                                            "cause ${exception.cause}, " +
                                            "message ${exception.message}, " +
                                            "stacktrace ${exception.stackTrace.joinToString("\n")}"
                                )
                                doneUI(
                                    true,
                                    DELETE_DATA,
                                    "delete failed [${exception.errorCode}]\nCheck your internet connection."
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                doneUI(
                    true,
                    DELETE_DATA,
                    "Unknown ${e.javaClass.toString().substring(6)}"
                )
            }
        }
    }
}