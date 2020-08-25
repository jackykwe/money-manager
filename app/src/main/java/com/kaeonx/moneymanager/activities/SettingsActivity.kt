package com.kaeonx.moneymanager.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.preference.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.SettingsActivityBinding
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.work.UploadDataWorker
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    internal lateinit var binding: SettingsActivityBinding

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Activity
     */
    ////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        // This must be called before the onCreate. If not, onStart will run twice!
        when (val value = UserPDS.getDSPString("dsp_theme", "light")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> throw IllegalArgumentException("Unknown dsp_theme $value")
        }

        super.onCreate(savedInstanceState)

        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    @Keep
    class PreferencesUserProfileFragment : PreferenceFragmentCompat() {

        private val settingsViewModel: SettingsViewModel by activityViewModels()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.user_profile_preferences, rootKey)

            Firebase.auth.currentUser!!.isAnonymous.let {
                findPreference<Preference>("acc_desc0")!!.isVisible = it
                findPreference<PreferenceCategory>("acc_category_account_settings")!!
                    .isEnabled = !it
            }

            findPreference<EditTextPreference>("acc_account_name")!!
                .setOnPreferenceChangeListener { _, newValue ->
                    val view = (requireActivity() as SettingsActivity).binding.root
                    Snackbar.make(
                        view,
                        "Updating name… Please wait.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setBehavior(NoSwipeBehaviour())
                        .show()
                    settingsViewModel.updateName(newValue as String)
                        .addOnSuccessListener {
                            Snackbar.make(
                                view,
                                "Successfully updated name",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { exception ->
                            when (exception) {
                                is FirebaseNetworkException -> {
                                    Snackbar.make(
                                        view,
                                        "Unable to update name.\nPlease check your internet connection.",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                                else -> {
                                    Snackbar.make(
                                        view,
                                        "Unable to update name [Unknown error].\nPlease report this bug.",
                                        Snackbar.LENGTH_INDEFINITE
                                    )
                                        .setBehavior(NoSwipeBehaviour())
                                        .show()
                                }
                            }
                        }
                    true
                }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            settingsViewModel.currentUser.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                if (!it.isAnonymous) {
                    findPreference<EditTextPreference>("acc_account_name")!!.apply {
                        summary = it.displayName
                        text = it.displayName
                    }
                }
            }
            super.onViewCreated(view, savedInstanceState)  // IMPORTANT
        }
    }

    @Keep
    class PreferencesCurrencyFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.currency_preferences, rootKey)
        }
    }

    @Keep
    class PreferencesCurrencyConverterFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.currency_converter_preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            XERepository.getInstance().xeRows.observe(viewLifecycleOwner) {
                val timestamp = it.getOrNull(0)?.updateMillis
                if (timestamp == null) {
                    findPreference<Preference>("ccv_active_table_stats")!!.summary =
                        "No table found. Please enable internet connection for the app to retrieve one from the internet."
                } else {
                    val dateFormat = UserPDS.getString("dsp_date_format")
                    val timeFormat = UserPDS.getString("dsp_time_format")
                    val lastUpdated = CalendarHandler.getFormattedString(
                        timestamp,
                        "$timeFormat 'on' $dateFormat"
                    )
                    findPreference<Preference>("ccv_active_table_stats")!!.summary =
                        "Last updated: $lastUpdated"
                }
            }
            super.onViewCreated(view, savedInstanceState)  // IMPORTANT
        }
    }

    @Keep
    class PreferencesDataAndPrivacyFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.data_and_privacy_preferences, rootKey)

            Firebase.auth.currentUser!!.isAnonymous.let {
                findPreference<Preference>("dap_desc0")!!.isVisible = it
                findPreference<PreferenceCategory>("dap_category_automatic_backup")!!
                    .isEnabled = !it
                findPreference<PreferenceCategory>("dap_category_delete_account")!!
                    .isEnabled = !it
            }

            findPreference<SwitchPreference>("dap_auto_backup_enabled")!!.apply {
                setOnPreferenceChangeListener { _, _ ->
                    GlobalScope.launch {
                        delay(1000L)  // to allow the Preference to update
                        UploadDataWorker.overwriteWork()
                    }
                    true
                }
            }

            findPreference<ListPreference>("dap_auto_backup_freq")!!.apply {
                setOnPreferenceChangeListener { _, _ ->
                    GlobalScope.launch {
                        delay(1000L)  // to allow the Preference to update
                        UploadDataWorker.overwriteWork()
                    }
                    true
                }
            }
        }
    }

    @Keep
    class PreferencesDisplayFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.display_preferences, rootKey)

            findPreference<ListPreference>("dsp_theme")!!.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    UserPDS.putDSPString("dsp_theme", newValue as String)
                    if (newValue != value) {
                        requireActivity().let { activity ->
                            activity.lifecycleScope.launch(Dispatchers.Main) {
                                delay(300L)
                                activity.recreate()
                            }
                        }
                    }
                    true
                }
            }
        }
    }

    @Keep
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

    @Keep
    class PreferencesPageBudgetFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.page_budget_preferences, rootKey)
        }
    }

    @Keep
    class PreferencesPageDebtsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = UserPDS
            setPreferencesFromResource(R.xml.page_debts_preferences, rootKey)
        }
    }

}