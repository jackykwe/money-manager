package com.kaeonx.moneymanager.fragments.transactions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.customclasses.fixCursorFocusProblems
import com.kaeonx.moneymanager.databinding.DialogFragmentTransactionsBsdfBinding
import com.kaeonx.moneymanager.fragments.accounts.ACCOUNTS_DF_RESULT
import com.kaeonx.moneymanager.fragments.categories.CATEGORIES_DF_RESULT
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.Account
import com.kaeonx.moneymanager.userrepository.domain.Category
import java.util.*

class TransactionsBSDF : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFragmentTransactionsBsdfBinding

    private val args: TransactionsBSDFArgs by navArgs()
    private val viewModelFactory by lazy { TransactionsBSDFViewModelFactory(args.oldTransaction) }
    private val viewModel: TransactionsBSDFViewModel by viewModels { viewModelFactory }

    private val savedStateHandle by lazy { findNavController().getBackStackEntry(R.id.transactionsBSDF).savedStateHandle }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentTransactionsBsdfBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        // Courtesy of https://stackoverflow.com/a/59748011/7254995
        val dialog = object : BottomSheetDialog(requireContext()) {
            override fun cancel() {
                if (viewModel.changesWereMade()) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Abandon unsaved changes?")
                        .setPositiveButton(R.string.ok) { _, _ -> dismiss() }
                        .setNegativeButton(R.string.cancel) { _, _ -> }
                        .create()
                        .show()
                } else {
                    dismiss()
                }
            }
        }
//        dialog.behavior.skipCollapsed = true
        dialog.behavior.isHideable = false
        dialog.behavior.peekHeight = 1980
        dialog.setOnShowListener {
            // When you set STATE_EXPANDED: the corners will animate away. This was intended by Material. https://stackoverflow.com/questions/43852562/round-corner-for-bottomsheetdialogfragment#comment104183618_57627229
            // Expands dialog fully when created: https://stackoverflow.com/questions/35937453/set-state-of-bottomsheetdialogfragment-to-expanded
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    // Courtesy of https://stackoverflow.com/a/57449808
    private fun pickDateTime(calendar: Calendar) {
        val startYear = calendar.get(Calendar.YEAR)
        val startMonth = calendar.get(Calendar.MONTH)
        val startDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val startHourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val startMinute = calendar.get(Calendar.MINUTE)


        DatePickerDialog(
            requireContext(),
            if (UserPDS.getString("dsp_theme") == "light") R.style.ThemeOverlay_MaterialComponents_Dialog_Alert else 0,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                viewModel.updateCalendar(calendar)

                TimePickerDialog(
                    requireContext(),
                    if (UserPDS.getString("dsp_theme") == "light") R.style.ThemeOverlay_MaterialComponents_Dialog_Alert else 0,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        viewModel.updateCalendar(calendar)
                    },
                    startHourOfDay,
                    startMinute,
                    true
                ).show()

            },
            startYear,
            startMonth,
            startDayOfMonth
        ).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tbsdMemoET.fixCursorFocusProblems()

        val currencyArray = resources.getStringArray(R.array.ccc_home_currency_values)
        binding.tbsdCurrencyTV.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Select Currency")
                .setSingleChoiceItems(
                    R.array.ccc_home_currency_entries,
                    currencyArray.indexOf(viewModel.currentTransaction.value!!.originalCurrency)
                ) { pickerDialog, which ->
                    viewModel.updateCurrency(currencyArray[which])
                    pickerDialog.dismiss()
                }
                .create()
                .show()
        }

        binding.tbsdBTDateTime.setOnClickListener {
            pickDateTime(
                CalendarHandler.getCalendar(viewModel.currentTransaction.value!!.timestamp)
            )
        }
        binding.tbsdAccountTV.setOnClickListener {
            findNavController().run {
                if (currentDestination?.id == R.id.transactionsBSDF) {
                    navigate(TransactionsBSDFDirections.actionTransactionsBSDFToAccountsDF())
                }
            }
        }
        binding.tbsdIconInclude.iconRing.setOnClickListener {
            findNavController().run {
                if (currentDestination?.id == R.id.transactionsBSDF) {
                    navigate(TransactionsBSDFDirections.actionTransactionsBSDFToAccountsDF())
                }
            }
        }
        binding.tbsdCategoryTV.setOnClickListener {
            findNavController().run {
                if (currentDestination?.id == R.id.transactionsBSDF) {
                    navigate(TransactionsBSDFDirections.actionTransactionsBSDFToCategoriesDF())
                }
            }
        }
        binding.tbsdIconInclude.iconBG.setOnClickListener {
            findNavController().run {
                if (currentDestination?.id == R.id.transactionsBSDF) {
                    navigate(TransactionsBSDFDirections.actionTransactionsBSDFToCategoriesDF())
                }
            }
        }

        viewModel.showToastText.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            viewModel.toastShown()
        }

        viewModel.navigateUp.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.navigateUpHandled()
                findNavController().navigateUp()
            }
        }

        savedStateHandle.getLiveData<Account?>(ACCOUNTS_DF_RESULT).observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.updateAccount(it)
                savedStateHandle.set(ACCOUNTS_DF_RESULT, null)
            }
        }

        savedStateHandle.getLiveData<Category?>(CATEGORIES_DF_RESULT).observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.updateCategory(it)
                savedStateHandle.set(CATEGORIES_DF_RESULT, null)
            }
        }
    }
}

