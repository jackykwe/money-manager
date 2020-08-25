package com.kaeonx.moneymanager.fragments.importexport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentCloudBinding
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.UserPDS

// TODO: add database version to JSON, for future migration

class CloudFragment : Fragment() {

    private lateinit var binding: FragmentCloudBinding
    private val viewModel: CloudFragmentViewModel by viewModels()

    private fun refreshLastUploadedTV() {
        binding.lastUploadedTV.text =
            if (UserPDS.getDSPBoolean("outdated_login", false)) {
                "Please re-login to enable Cloud Backup."
            } else {
                val lastUploadTime = UserPDS.getDSPLong(
                    "${Firebase.auth.currentUser!!.uid}_last_upload_time",
                    -1L
                )
                when (lastUploadTime) {
                    -1L ->
                        if (Firebase.auth.currentUser!!.isAnonymous) {
                            "Cloud Backup unavailable for Guests"
                        } else {
                            getString(R.string.no_cloud_data_found)
                        }
                    else -> {
                        binding.resultIV.alpha = 0.5f
                        val dateFormat = UserPDS.getString("dsp_date_format")
                        val timeFormat = UserPDS.getString("dsp_time_format")
                        CalendarHandler.getFormattedString(
                            lastUploadTime,
                            "'Last uploaded:' $timeFormat 'on' $dateFormat"
                        )
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentCloudBinding.inflate(inflater, container, false)
        binding.uploadBT.apply {
            if (Firebase.auth.currentUser!!.isAnonymous) {
                isEnabled = false
            } else {
                setOnClickListener { viewModel.uploadData() }
            }
        }
        binding.deleteDataBT.apply {
            if (Firebase.auth.currentUser!!.isAnonymous) {
                isEnabled = false
            } else {
                setOnClickListener { viewModel.deleteData() }
            }
        }
        return binding.root
    }

    private fun startUI() {
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

    private fun updateUI(newProgressText: String, newProgress: Int) {
        if (newProgressText.takeLast(1) != "…") throw IllegalArgumentException("newProgressText must end with …")
        binding.progressPI.setProgress(newProgress, true)
        binding.progressTV.text = newProgressText
    }

    private fun doneUI(resultIVDrawableId: Int, progressTVText: String, exceptionText: String?) {
        binding.uploadBT.isEnabled = true
        binding.deleteDataBT.isEnabled = true

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
            binding.warningTV.text = exceptionText
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.refreshLastUploadedTV.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.refreshLastUploadedTVHandled()
                refreshLastUploadedTV()
            }
        }

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

    }
}