package com.kaeonx.moneymanager.fragments.transactions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaeonx.moneymanager.customclasses.fixCursorFocusProblems
import com.kaeonx.moneymanager.databinding.DialogFragmentTransactionsBsdfBinding
import kotlinx.android.synthetic.main.dialog_fragment_transactions_bsdf.*
import kotlinx.android.synthetic.main.icon_transaction.*
import java.util.*

private const val TAG = "TBSDF"

class TransactionsBSDF : BottomSheetDialogFragment() {
//    BottomSheetDialogFragment(),
//    TimePickerDialogFragment.TimePickerListener,
//    DatePickerDialogFragment.DatePickerListener,
//    CategoryPickerDialogFragment.CatPickerListener,
//    AccountDisplayDialogFragment.AccountPickerListener {

    private val args: TransactionsBSDFArgs by navArgs()
    private val viewModelFactory by lazy { TransactionsBSDFViewModelFactory(requireActivity().application, args.oldTransaction) }
    private val viewModel: TransactionsBSDFViewModel by viewModels { viewModelFactory }

    private lateinit var binding: DialogFragmentTransactionsBsdfBinding

    private fun setAccountColor(color: Int) {
        tbsdHorizontalBarIVTop.drawable.setTint(color)
        iconRing.drawable.setTint(color)
    }

    private fun setCategoryColor(color: Int) {
        tbsdHorizontalBarIVBottom.drawable.setTint(color)
        iconBG.drawable.setTint(color)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFragmentTransactionsBsdfBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.tbsdMemoET.fixCursorFocusProblems()

        binding.tbsdBTDateTime.setOnClickListener {
            pickDateTime(viewModel.calendar.value!!)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        // Courtesy of https://stackoverflow.com/a/59748011/7254995
        val dialog = object : BottomSheetDialog(requireContext()) {
            override fun cancel() {
                Log.d(TAG, "BSDF cancelled")
                dismiss()
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

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->

            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                viewModel.updateCalendar(calendar)
            }, startHourOfDay, startMinute, true).show()

        }, startYear, startMonth, startDayOfMonth).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
/*
//        // Setup TBSD view
//        if (oldTransaction == null) {
//            // New transaction
//            // Set default colours here
//
//            // ACCOUNT
////            val defaultAccount = AccountHandler.getAccounts(requireContext(), firebaseViewModel.currentUserLD.value!!.uid)[0]
//            val defaultAccountName = PreferenceDS.getString2("tst_default_account")
//            val accountsAL = AccountHandler.getAccounts((requireActivity() as MainActivity).loadedUserId!!).filter { it.name == defaultAccountName }
//            val defaultAccount = if (accountsAL.size == 1) accountsAL[0] else throw IllegalStateException("defaultAccount specified in preferences does not exist in accounts. Did you handle defaultAccount changes correctly?")
//            setAccountColor(ColourHandler.getColourObject(resources, defaultAccount.colourString))
//            tbsdAccountTV.text = defaultAccount.name
//
//            // TYPE & CATEGORY
//            setCategoryColor(ColourHandler.getColourObject(resources, "Grey", "600"))
//
//            // MILLIS
//            tbsdBTDateTime.text = displayTimeAndDate(currentCalendar)  // currentCalendar is lazily evaluated/initialised here
//
//            // CURRENCY
//            tbsdCurrencyTV.text = PreferenceDS.getString2("ccc_home_currency")
//
//            // MEMO, AMOUNT
//            // (NOT APPLICABLE)
//
//            mode = TransactionBSDFMode.NEW
//        } else {
//            // Editing transaction
//
//            // ACCOUNT
//            setAccountColor(
//                ColourHandler.getColourObject(
//                    resources,
//                    AccountHandler.getAccount(
//                        (requireActivity() as MainActivity).loadedUserId!!,
//                        oldTransaction.account
//                    ).colourString
//                )
//            )
//            tbsdAccountTV.text = oldTransaction.account
//
//            // TYPE & CATEGORY
//            val oldCategory = CategoryIconHandler.getCategory(
//                (requireActivity() as MainActivity).loadedUserId!!,
//                oldTransaction.type,
//                oldTransaction.category
//            )
//            onCategoryPickerResult(oldTransaction.type, oldCategory)
//
//            // MILLIS
//            currentCalendar.timeInMillis = oldTransaction.timestamp  // currentCalendar is lazily evaluated/initialised here
//            tbsdBTDateTime.text = displayTimeAndDate(currentCalendar)
//
//            // CURRENCY
//            tbsdCurrencyTV.text = oldTransaction.originalCurrency
//
//            // MEMO, AMOUNT
//            tbsdMemoET.setText(oldTransaction.memo)
//            viewModel.declareOperand1(oldTransaction.originalAmount)
//            viewModel.declareExistingTransaction()
//
//            mode = TransactionBSDFMode.EDIT
//        }
*/



//        // currencyTV listener
//        tbsdCurrencyTV.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("Select Currency")
//                .setItems(R.array.ccc_currencies_entries) { _, which -> // TODO: Truncate list (hide unused currencies - depends on which one the currency converter supports too)
//                    val currencyCode = resources.getStringArray(R.array.ccc_currencies_values).toList()[which]
//                    tbsdCurrencyTV.text = currencyCode
//                }
//                .create()
//                .show()
//        }

        viewModel.showToastText.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.toastShown()
            }
        }

//
//        tbsdAccountTV.setOnClickListener {
//            AccountDisplayDialogFragment(
//                this,
//                false
//            ).show(childFragmentManager, null)
//        }
//        tbsdCategoryTV.setOnClickListener {
//            CategoryPickerDialogFragment(
//                this
//            ).show(childFragmentManager, null)
//        }
    }

//    override fun onCategoryPickerResult(type: String, category: Category) {
//        chosenType = type
//        tbsdTypeTV.text = when (type) {
//            "Income" -> hexToIcon("F0048")
//            "Expenses" -> hexToIcon("F0060")
//            else -> throw java.lang.IllegalStateException("Unknown category $type")
//        }
//        tbsdCategoryTV.text = category.name
//        iconTV.text = hexToIcon(category.iconHex)
//        setCategoryColor(ColourHandler.getColourObject(resources, category.colourString))
//
//        viewModel.updateCategoriesAreNullMLD()
//    }
//
//    override fun onAccountSelected(account: Account) {
//        tbsdAccountTV.text = account.name
//        setAccountColor(ColourHandler.getColourObject(resources, account.colourString))
//    }

//    private fun submitTransactionAndDismiss() {
//        val transaction = Transaction(
//            id=oldTransaction?.id,
//            timestamp=currentCalendar.timeInMillis,
//            timeZone=currentCalendar.timeZone.id,
//            type=chosenType ?: throw java.lang.IllegalStateException("You shouldn't reach this code! type must not be null"),
//            category=tbsdCategoryTV.text.toString(),
//            account=tbsdAccountTV.text.toString(),
//            memo=tbsdMemoET.text.toString().trim(),
//            originalCurrency=tbsdCurrencyTV.text.toString(),
//            originalAmount=CurrencyHandler.bigDecimalDisplay(tbsdAmountTV.text.toString().toBigDecimal())
//        )
//        Log.d(TAG, "tbsdAmountTV.text.toString() = ${tbsdAmountTV.text.toString()}")
//        val result = when (mode) {
//            TransactionBSDFMode.NEW -> {
//                Log.d(TAG, "Saving transaction: ${CalendarHandler.getFormattedString(currentCalendar, "HHmmss:S ddMMyy")}")
//                JSONHandler.addTransaction((requireActivity() as MainActivity).loadedUserId!!, transaction)
//            }
//            TransactionBSDFMode.EDIT -> {
//                Log.d(TAG, "Editing transaction: ${CalendarHandler.getFormattedString(currentCalendar, "HHmmss:S ddMMyy")}")
//                JSONHandler.modifyTransaction((requireActivity() as MainActivity).loadedUserId!!, transaction)
//            }
//        }
//        listener.onTBSDFResult(result, transaction)
//        dismiss()
//    }
}

