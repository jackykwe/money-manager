package com.kaeonx.moneymanager.userrepository

import android.util.Log
import androidx.preference.PreferenceDataStore
import com.kaeonx.moneymanager.userrepository.domain.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "pds"

object UserPDS : PreferenceDataStore() {

    init {
        Log.d(TAG, "UserPDS instantiated")
    }

    private val userRepository = UserRepository.getInstance()
    private val preferences = userRepository.preferences

    private val defaultPreferences = mapOf(
        // Account
        "acc_account_name" to "Cash",  // TODO handle when this account is deleted.

        // Currency
        "ccc_home_currency" to "SGD",
        "ccc_hide_matching_currency" to true,

        // Currency Converter
        "ccv_enable_online" to true,
        "ccv_online_update_ttl" to "86400000",

        // Data and Privacy
        "dap_backup_freq" to "24",

        // Display
        "dsp_theme" to "coloured",
        "dsp_date_format" to "ddMMyy",
        "dsp_time_format" to "HHmm",

        // Page Transactions
        "tst_default_account" to "Cash",
        "tst_default_type" to "Expenses"

        // Page Budget
        // NONE

        // Page Debts
        // NONE
    )

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return if (preferences.value == null) {
            Log.d(
                TAG,
                "getBoolean: preferences.value is null - UserRepository.preferences not yet ready"
            )
            defaultPreferences[key] as Boolean
        } else if (preferences.value!![key] == null) {
            Log.d(TAG, "getBoolean: preferences.value!![key] is null - key not found?? Nani")
            defaultPreferences[key] as Boolean
        } else {
            (preferences.value!![key] as Int).toBooleanNullable()!!
        }
//        return (preferences.value?.get(key) as Int?).toBooleanNullable() ?: defaultPreferences[key] as Boolean
    }

    fun getBoolean(key: String): Boolean {
        return if (preferences.value == null) {
            Log.d(
                TAG,
                "getBoolean: preferences.value is null - UserRepository.preferences not yet ready"
            )
            defaultPreferences[key] as Boolean
        } else if (preferences.value!![key] == null) {
            Log.d(TAG, "getBoolean: preferences.value!![key] is null - key not found?? Nani")
            defaultPreferences[key] as Boolean
        } else {
            (preferences.value!![key] as Int).toBooleanNullable()!!
        }
//        return (preferences.value?.get(key) as Int?).toBooleanNullable() ?: defaultPreferences[key] as Boolean
    }

    private fun Int?.toBooleanNullable(): Boolean? = when (this) {
        null -> this
        1 -> true
        0 -> false
        else -> throw IllegalArgumentException("Unknown boolean value $this")
    }

    override fun putBoolean(key: String, value: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.upsertPreference(
                Preference(
                    key = key,
                    valueInteger = if (value) 1 else 0,
                    valueText = null
                )
            )
        }
    }

    override fun getString(key: String, defValue: String?): String {
        Log.d(TAG, "getString: called")
        return if (preferences.value == null) {
            Log.d(
                TAG,
                "getString: preferences.value is null - UserRepository.preferences not yet ready"
            )
            defaultPreferences[key] as String
        } else if (preferences.value!![key] == null) {
            Log.d(TAG, "getString: preferences.value!![key] is null - key not found?? Nani")
            defaultPreferences[key] as String
        } else {
            preferences.value!![key] as String
        }
//        return preferences.value?.get(key) as String? ?: defaultPreferences[key] as String
    }

    fun getString(key: String): String {
        Log.d(TAG, "getString: called")
        return if (preferences.value == null) {
            Log.d(
                TAG,
                "getString: preferences.value is null - UserRepository.preferences not yet ready"
            )
            defaultPreferences[key] as String
        } else if (preferences.value!![key] == null) {
            Log.d(TAG, "getString: preferences.value!![key] is null - key not found?? Nani")
            defaultPreferences[key] as String
        } else {
            preferences.value!![key] as String
        }
//        return preferences.value?.get(key) as String? ?: defaultPreferences[key] as String
    }

    override fun putString(key: String, value: String?) {
        if (value == null) throw IllegalArgumentException("putString: value cannot be null")
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.upsertPreference(
                Preference(
                    key = key,
                    valueInteger = null,
                    valueText = value
                )
            )
        }
    }
}