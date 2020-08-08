package com.kaeonx.moneymanager.fragments.transactions

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.databinding.DialogFragmentYearPickerBinding
import java.util.*

internal const val MY_PICKER_RESULT = "my_picker"

class MonthYearPickerDialogFragment : DialogFragment() {

    private val args: MonthYearPickerDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
        val binding = DialogFragmentYearPickerBinding.inflate(layoutInflater)
        binding.monthNP.apply {
            minValue = 0
            maxValue = 11
            displayedValues = arrayOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )
            value = args.currentCalendar.get(Calendar.MONTH)
        }
        binding.yearNP.apply {
            minValue = 1970
            maxValue = 2099
            value = args.currentCalendar.get(Calendar.YEAR)
        }

        val todayCalendar = Calendar.getInstance()
        val resetArray = arrayOf(
            todayCalendar.get(Calendar.MONTH),
            todayCalendar.get(Calendar.YEAR)
        )

        return AlertDialog
            .Builder(activity as Context)
            .setView(binding.root)
            .setTitle("Select Year")
            .setPositiveButton("OK") { _, _ ->
                findNavController().getBackStackEntry(args.originFragmentId).savedStateHandle.set(
                    MY_PICKER_RESULT, arrayOf(binding.monthNP.value, binding.yearNP.value)
                )
                findNavController().navigateUp()
            }
            .setNegativeButton("CANCEL") { _, _ -> }
            .setNeutralButton("TODAY") { _, _ ->
                findNavController().getBackStackEntry(args.originFragmentId).savedStateHandle.set(
                    MY_PICKER_RESULT, resetArray
                )
                findNavController().navigateUp()
            }
            .create()
    }
}