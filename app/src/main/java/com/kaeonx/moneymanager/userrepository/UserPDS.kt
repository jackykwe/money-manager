package com.kaeonx.moneymanager.userrepository

import androidx.preference.PreferenceDataStore
import com.kaeonx.moneymanager.userrepository.domain.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UserPDS : PreferenceDataStore() {

    private val userRepository = UserRepository.getInstance()
    private val preferences = userRepository.preferences

    internal fun getAllValidKeys() = defaultPreferences.keys

    private val defaultPreferences = mapOf(
        // Account
        "acc_account_name" to "NAMEHM",

        // Currency
        "ccc_home_currency" to "SGD",
        "ccc_hide_matching_currency" to true,

        // Currency Converter
        "ccv_enable_online" to true,
        "ccv_online_update_ttl" to "86400000",

        // Data and Privacy
        "dap_auto_backup_enabled" to true,
        "dap_auto_backup_freq" to "1",

        // Display
        "dsp_theme" to "light",
        "dsp_date_format" to "ddMMyy",
        "dsp_time_format" to "HHmm",
        "dsp_sign_position" to "after_currency",

        // Page Transactions
        "tst_default_account" to "Cash",  // TODO handle when this account is deleted. (& handle budgets when category is deleted)
        "tst_default_type" to "Expenses"

        // Page Budget
        // NONE

        // Page Debts
        // NONE
    )

    override fun getBoolean(key: String, defValue: Boolean): Boolean =
        (preferences.value!![key] as Int?).toBooleanNullable() ?: defaultPreferences[key] as Boolean

    fun getBoolean(key: String): Boolean =
        (preferences.value!![key] as Int?).toBooleanNullable() ?: defaultPreferences[key] as Boolean

    fun getDefaultBoolean(key: String): Boolean = defaultPreferences[key] as Boolean

    private fun Int?.toBooleanNullable(): Boolean? = when (this) {
        null -> this
        1 -> true
        0 -> false
        else -> throw IllegalArgumentException("Unknown boolean value $this")
    }

    override fun putBoolean(key: String, value: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.upsertPreferenceSuspend(
                Preference(
                    key = key,
                    valueInteger = if (value) 1 else 0,
                    valueText = null
                )
            )
        }
    }

    override fun getString(key: String, defValue: String?): String =
        preferences.value!![key] as String? ?: defaultPreferences[key] as String

    internal fun getString(key: String): String =
        preferences.value!![key] as String? ?: defaultPreferences[key] as String

    internal fun getDefaultString(key: String): String = defaultPreferences[key] as String

    override fun putString(key: String, value: String?) {
        if (value == null) throw IllegalArgumentException("putString: value cannot be null")
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.upsertPreferenceSuspend(
                Preference(
                    key = key,
                    valueInteger = null,
                    valueText = value
                )
            )
        }
    }
}