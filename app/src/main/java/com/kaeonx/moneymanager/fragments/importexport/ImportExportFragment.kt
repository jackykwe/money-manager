package com.kaeonx.moneymanager.fragments.importexport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.databinding.FragmentImportExportBinding
import com.kaeonx.moneymanager.importexport.IEFileHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "inexfrag"

class ImportExportFragment : Fragment() {

    private lateinit var binding: FragmentImportExportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportExportBinding.inflate(inflater, container, false)
        binding.progressTV.text = "No active tasks"
        binding.progressPI.hide()
        binding.exportBT.setOnClickListener {
            startActivityForResult(
                IEFileHandler.constructWriteFileIntent("output"),
                IEFileHandler.OUTPUT_TO_FILE
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
        binding.progressPI.show()

        // TVs
        binding.warningTV.animate()
            .alpha(1f)
            .setDuration(
                resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            )
            .setListener(null)
    }


    private suspend fun updateUI(newProgressText: String, newProgress: Int) {
        withContext(Dispatchers.Main) {
            binding.progressPI.setProgress(newProgress, true)
            binding.progressTV.text = newProgressText
        }
    }

    private fun doneUI(error: Boolean, exceptionText: String?) {
        binding.importBT.isEnabled = true
        binding.exportBT.isEnabled = true

        // resultIV and progressPI
        binding.progressPI.hide()
        binding.resultIV.setImageDrawable(
            if (error) {
                resources.getDrawable(
                    R.drawable.mdi_alert_circle_amber,
                    requireActivity().theme
                )
            } else {
                resources.getDrawable(
                    R.drawable.mdi_check_circle_green,
                    requireActivity().theme
                )
            }
        )
        binding.resultIV.animate()
            .alpha(1f)
            .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
            .setListener(null)

        // TVs
        binding.progressTV.text =
            if (error) "Unable to save data because of" else "Successfully exported data"
        if (error) {
            binding.warningTV.text = exceptionText
        } else {
            binding.warningTV.animate()
                .alpha(0f)
                .setDuration(
                    resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                )
                .setListener(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "received requestCode $requestCode, resultCode $resultCode")
        if (requestCode == IEFileHandler.OUTPUT_TO_FILE && resultCode == Activity.RESULT_OK) {
            startUI()
            lifecycleScope.launch(Dispatchers.Default) {
                val output = JSONObject()
                val repository = UserRepository.getInstance()
                delay(500L)
                // Transactions
                Log.d(TAG, "step 1")
//                val list = repository.exportTransactionsSuspend()
                Log.d(TAG, "step 2")
//                val jsonArray = IETransactionsHandler.listToJsonArray(list)
                Log.d(TAG, "step 3")
//                output.put("transactions", jsonArray)
                // grab from db, convert,
                updateUI("Grabbing Transactions...", 10)
                // Budget
                delay(500L)
                updateUI("Grabbing Budgets...", 20)
                // Debts (TODO)
                delay(500L)
                updateUI("Grabbing Debts...", 30)
                // Categories
                delay(500L)
                updateUI("Grabbing Categories...", 40)
                // Accounts
                delay(500L)
                updateUI("Grabbing Accounts...", 50)
                IEFileHandler.writeJSONString(
                    contentResolver = requireContext().contentResolver,
                    data = data,
                    contentToWrite = output.toString(4)
                )
                // Settings
                delay(500L)
                updateUI("Grabbing Settings...", 60)
                withContext(Dispatchers.Main) { doneUI(false, null) }
            }
        }
    }
}