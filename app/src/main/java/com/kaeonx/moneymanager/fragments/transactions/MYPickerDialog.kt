package com.kaeonx.moneymanager.fragments.transactions

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.kaeonx.moneymanager.databinding.DialogFragmentYearPickerBinding
import java.util.*

class MYPickerDialog private constructor() {

    companion object {

        /**
         * There is no need to clone the `initCalendar`, since no changes will be made to it.
         */
        fun createDialog(
            context: Context,
            initCalendar: Calendar,
            resultListener: MYPickerDialogListener
        ): Dialog {
            val binding = DialogFragmentYearPickerBinding.inflate(LayoutInflater.from(context))
            binding.monthNP.apply {
                minValue = 0
                maxValue = 11
                displayedValues = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )
                value = initCalendar.get(Calendar.MONTH)
            }
            binding.yearNP.apply {
                minValue = 1970
                maxValue = 2099
                value = initCalendar.get(Calendar.YEAR)
            }

            val todayCalendar = Calendar.getInstance()
            val resetArray = arrayOf(
                todayCalendar.get(Calendar.MONTH),
                todayCalendar.get(Calendar.YEAR)
            )

            return AlertDialog
                .Builder(context)
                .setView(binding.root)
                .setTitle("Select Year")
                .setPositiveButton("OK") { _, _ ->
                    resultListener.onClick(arrayOf(binding.monthNP.value, binding.yearNP.value))
                }
                .setNegativeButton("CANCEL") { _, _ -> }
                .setNeutralButton("TODAY") { _, _ ->
                    resultListener.onClick(resetArray)
                }
                .create()
        }
    }

    class MYPickerDialogListener(private val onClickListener: (Array<Int>) -> Unit) {
        fun onClick(result: Array<Int>) = onClickListener(result)
    }
}