package com.kaeonx.moneymanager.fragments.importexport

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentImportExportBinding
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.OUTPUT_TO_FILE
import com.kaeonx.moneymanager.fragments.importexport.iehandlers.IEFileHandler.Companion.READ_FROM_FILE
import com.kaeonx.moneymanager.handlers.CalendarHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImportExportFragment : Fragment() {

    private lateinit var binding: FragmentImportExportBinding
    private val viewModel: ImportExportViewModel by viewModels()

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

    private fun updateUI(newProgressText: String, newProgress: Int) {
        if (newProgressText.takeLast(1) != "…") throw IllegalArgumentException("newProgressText must end with …")
        binding.progressPI.setProgress(newProgress, true)
        binding.progressTV.text = newProgressText
    }

    private fun doneUI(resultIVDrawableId: Int, progressTVText: String, exceptionText: String?) {
        binding.importBT.isEnabled = true
        binding.exportBT.isEnabled = true

        // resultIV and progressPI
        binding.progressPI.animate()
            .alpha(0f)
            .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
            .setListener(null)
        binding.resultIV.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                resultIVDrawableId
            )
        )

        binding.resultIV.animate()
            .alpha(1f)
            .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
            .setListener(null)

        // TVs
        binding.progressTV.text = progressTVText
        if (exceptionText == null) {
            binding.warningTV.animate()
                .alpha(0f)
                .setDuration(
                    resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                )
                .setListener(null)
        } else {
            binding.warningTV.text = exceptionText.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.startUI.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.startUIHandled()
                startUI()
            }
        }

        viewModel.updateUI.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            viewModel.updateUIHandled()
            updateUI(it.first, it.second)
        }

        viewModel.doneUI.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            viewModel.doneUIHandled()
            doneUI(it.resultIVDrawableId, it.progressTVText, it.exceptionText)
        }

        viewModel.showApplyingThemeSnackbar.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.showApplyingThemeSnackbarHandled()
                requireActivity().let { activity ->
                    activity.lifecycleScope.launch(Dispatchers.Main) {
                        Snackbar.make(
                            (activity as MainActivity).binding.root,
                            "Applying theme…",
                            Snackbar.LENGTH_LONG
                        ).show()
                        delay(1000L)
                        activity.recreate()
                    }
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OUTPUT_TO_FILE && resultCode == Activity.RESULT_OK) {
            viewModel.exportData(data)
        } else if (requestCode == READ_FROM_FILE && resultCode == Activity.RESULT_OK) {
            AlertDialog.Builder(requireContext())
                .setTitle("Overwrite local data?")
                .setMessage(
                    "Any current data on this device will be overwritten. " +
                            "To avoid unexpected loss of data, do an export / cloud upload" +
                            " before continuing."
                )
                .setPositiveButton(R.string.ok) { _, _ ->
                    viewModel.importData(data)
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()
                .show()

        }
    }
}