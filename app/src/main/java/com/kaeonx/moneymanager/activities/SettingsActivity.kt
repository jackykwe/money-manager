package com.kaeonx.moneymanager.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.xerepository.XERepository

private const val TAG = "statvt"
private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Activity
     */
    ////////////////////////////////////////////////////////////////////////////////

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, PreferencesHomeFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.settings_activity_title)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        authViewModel.currentUser.observe(this) {
            if (it == null) {
                // Logout logic. Login logic is controlled from within RootTitleFragment.
                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment =
            supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment).apply {
                arguments = args
                setTargetFragment(caller, 0)
            }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    // Required to fix weird Up button behaviour (*always* refreshes MainActivity, which I don't want)
    // I only want MainActivity to refresh when RESULT_OK, a behaviour which is obeyed by the back button.
    // This function ties the Up button behaviour to the back button behaviour.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     *  PreferenceFragments
     */
    ////////////////////////////////////////////////////////////////////////////////

    class PreferencesHomeFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.preferences_home, rootKey)

        }
    }

    class PreferencesUserProfileFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.user_profile_preferences, rootKey)
        }
    }

    class PreferencesCurrencyFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.currency_preferences, rootKey)

            findPreference<ListPreference>("ccc_home_currency")!!.apply {
//                value = PreferenceDS.getString2(key)
                setOnPreferenceChangeListener { preference, newValue ->
                    Log.d(TAG, "I got changed lol")
//                    requireActivity().setResult(Activity.RESULT_OK)
                    true
                }
            }

            findPreference<SwitchPreference>("ccc_hide_matching_currency")!!.apply {
//                isChecked = PreferenceDS.getBoolean2(key)
                setOnPreferenceChangeListener { preference, newValue ->
                    Log.d(TAG, "I got changed lol2")
//                    requireActivity().setResult(Activity.RESULT_OK)
                    true
                }
            }
        }
    }

    class PreferencesCurrencyConverterFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.currency_converter_preferences, rootKey)
            val homeCurrency = UserPDS.getString("ccc_home_currency")
            val dateFormat = UserPDS.getString("dsp_date_format")
            val timeFormat = UserPDS.getString("dsp_time_format")
            val timestamp =
                XERepository.getInstance().xeRows.value!!.find { it.baseCurrency == homeCurrency }?.updateMillis
            if (timestamp == null) {
                findPreference<Preference>("ccv_active_table_stats")!!.summary =
                    "No active table. Please enable internet connection for the app to retrieve one from the internet."
            } else {
                val lastUpdated =
                    CalendarHandler.getFormattedString(timestamp, "$timeFormat 'on' $dateFormat")
                findPreference<Preference>("ccv_active_table_stats")!!.summary =
                    "Base currency: $homeCurrency\nLast updated: $lastUpdated"
            }
        }
    }

    class PreferencesDataAndPrivacyFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.data_and_privacy_preferences, rootKey)
        }
    }

    class PreferencesDisplayFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.display_preferences, rootKey)
        }
    }

    class PreferencesPageTransactionsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.page_transactions_preferences, rootKey)

            val savedAccounts =
                UserRepository.getInstance().accounts.value!!.map { it.name }.toTypedArray()
            findPreference<ListPreference>("tst_default_account")!!.apply {
                entries = savedAccounts
                entryValues = savedAccounts
            }
        }
    }

    class PreferencesPageBudgetFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.page_budget_preferences, rootKey)
        }
    }

    class PreferencesPageDebtsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.page_debts_preferences, rootKey)
        }
    }

}