package com.kaeonx.moneymanager.userrepository

import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceManager
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.userrepository.domain.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object UserPDS : PreferenceDataStore() {

    internal fun getAllValidKeys() = defaultPreferences.keys

    private val defaultPreferences = mapOf(
        // Account
        // NONE

        // Currency
        "ccc_home_currency" to "SGD",  // controlled by array
        "ccc_hide_matching_currency" to true,

        // Currency Converter
        "ccv_enable_online" to true,
        "ccv_online_update_ttl" to "86400000",  // controlled by array

        // Data and Privacy
        "dap_auto_backup_enabled" to true,
        "dap_auto_backup_freq" to "1",  // controlled by array

        // Display
        "dsp_theme" to "light",  // controlled by array
        "dsp_date_format" to "ddMMyy",  // controlled by array
        "dsp_time_format" to "HHmm",  // controlled by array
        "dsp_sign_position" to "after_currency",  // controlled by array

        // Page Transactions
        "tst_default_account" to "Cash",  // **NOT controlled by array**
        "tst_default_type" to "Expenses"  // controlled by array

        // Page Budget
        // NONE

        // Page Debts
        // NONE
    )

    override fun getBoolean(key: String, defValue: Boolean): Boolean =
        (UserRepository.getInstance().preferences.value!![key] as Int?).toBooleanNullable()
            ?: defaultPreferences[key] as Boolean

    fun getBoolean(key: String): Boolean =
        (UserRepository.getInstance().preferences.value!![key] as Int?).toBooleanNullable()
            ?: defaultPreferences[key] as Boolean

    fun getDefaultBoolean(key: String): Boolean = defaultPreferences[key] as Boolean

    private fun Int?.toBooleanNullable(): Boolean? = when (this) {
        null -> this
        1 -> true
        0 -> false
        else -> throw IllegalArgumentException("Unknown boolean value $this")
    }

    override fun putBoolean(key: String, value: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository.getInstance().upsertPreferenceSuspend(
                Preference(
                    key = key,
                    valueInteger = if (value) 1 else 0,
                    valueText = null
                )
            )
        }
    }

    override fun getString(key: String, defValue: String?): String =
        UserRepository.getInstance().preferences.value!![key] as String?
            ?: defaultPreferences[key] as String

    internal fun getString(key: String): String =
        UserRepository.getInstance().preferences.value!![key] as String?
            ?: defaultPreferences[key] as String

    internal fun getDefaultString(key: String): String = defaultPreferences[key] as String

    override fun putString(key: String, value: String?) {
        if (value == null) throw IllegalArgumentException("putString: value cannot be null")
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository.getInstance().upsertPreferenceSuspend(
                Preference(
                    key = key,
                    valueInteger = null,
                    valueText = value
                )
            )
        }
    }

    internal fun getDSPString(key: String, defaultValue: String): String =
        PreferenceManager.getDefaultSharedPreferences(App.context).getString(key, defaultValue)!!

    internal fun putDSPString(key: String, value: String): Boolean =
        PreferenceManager.getDefaultSharedPreferences(App.context)
            .edit()
            .putString(key, value)
            .commit()

    internal fun getDSPLong(key: String, defaultValue: Long) =
        PreferenceManager.getDefaultSharedPreferences(App.context).getLong(key, defaultValue)

    internal fun putDSPLong(key: String, value: Long): Boolean =
        PreferenceManager.getDefaultSharedPreferences(App.context)
            .edit()
            .putLong(key, value)
            .commit()

    internal fun getDSPBoolean(key: String, defaultValue: Boolean): Boolean =
        PreferenceManager.getDefaultSharedPreferences(App.context).getBoolean(key, defaultValue)

    internal fun putDSPBoolean(key: String, value: Boolean): Boolean =
        PreferenceManager.getDefaultSharedPreferences(App.context)
            .edit()
            .putBoolean(key, value)
            .commit()

    internal fun removeDSPKeyIfExists(key: String): Boolean =
        PreferenceManager.getDefaultSharedPreferences(App.context)
            .edit()
            .remove(key)
            .commit()

    internal fun removeAllDSPKeys(): Boolean =
        PreferenceManager.getDefaultSharedPreferences(App.context)
            .edit()
            .clear()
            .commit()

}