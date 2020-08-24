package com.kaeonx.moneymanager.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.BuildConfig
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.TopLevelNavGraphDirections
import com.kaeonx.moneymanager.customclasses.NoSwipeBehaviour
import com.kaeonx.moneymanager.databinding.ActivityMainBinding
import com.kaeonx.moneymanager.databinding.NavHeaderMainBinding
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.work.UploadDataWorker

private const val TAG = "matvt"
private const val START_SETTING_ACTIVITY = 0
private const val START_CLAIM_LOGIN_INTENT = 1

class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    internal lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var previousLoadedTheme: String

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MAIN ACTIVITY RECREATED")
        // This must be called before the onCreate. If not, onStart will run twice!
        previousLoadedTheme = UserPDS.getDSPString("dsp_theme", "light")
        when (previousLoadedTheme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> throw IllegalArgumentException("Unknown dsp_theme $previousLoadedTheme")
        }

        // Must be called before setContentView()
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        // For MPAndroidChart to draw correctly the first time round
        // Courtesy of https://stackoverflow.com/a/57349873/7254995
        Utils.init(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.transactionsFragment//,
//            R.id.budgetFragment,
//            R.id.debtFragment,
//            R.id.titleFragment
            ), binding.rootDL
        )

        binding.mainActivityNV.apply {
            getChildAt(0).isVerticalScrollBarEnabled = false
            if (previousLoadedTheme == "light") ColourHandler.getSpecificColorStateListOf("Black")
                .let {
                    itemBackground!!.setTintList(it)
                    itemIconTintList = it
                    itemTextColor = it
                }
            setupWithNavController(navController) // Connects navigation drawer to navController
            setNavigationItemSelectedListener {
                fun navigateToExitLobbyFragment() {
                    if (navController.currentDestination?.id !in listOf(
                            R.id.titleFragment,
                            R.id.lobbyFragment,
                            R.id.exitLobbyFragment
                        )
                    ) {
                        // Logout logic. Login logic is controlled from within RootTitleFragment.
                        navController.navigate(TopLevelNavGraphDirections.actionGlobalExitLobbyFragment())
                    }
                }

                binding.rootDL.closeDrawers()
                when (it.itemId) {
                    R.id.debtFragment -> {
                        false
                    }
                    R.id.menuSettings -> {
//                        Snackbar.make(binding.appBarMainInclude.mainActivityFAB, "Function not available yet", Snackbar.LENGTH_SHORT).show()
//                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        startActivityForResult(
                            Intent(
                                this@MainActivity,
                                SettingsActivity::class.java
                            ), START_SETTING_ACTIVITY
                        )
                        false
                    }
                    R.id.menuLogout -> {
                        if (Firebase.auth.currentUser!!.isAnonymous) {
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Logout and delete guest account?")
                                .setMessage("If you do not claim this account or export your data, all data associated with this guest accout will be lost forever on successful logout.")
                                .setPositiveButton(R.string.ok) { _, _ -> navigateToExitLobbyFragment() }
                                .setNegativeButton(R.string.cancel) { _, _ -> }
                                .create()
                                .show()
                        } else {
                            navigateToExitLobbyFragment()
                        }
                        false
                    }
                    R.id.menuAbout -> {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(R.string.app_name)
                            .setMessage("Version ${BuildConfig.VERSION_NAME}")
                            .create()
                            .show()
                        false
                    }
                    navController.currentDestination!!.id -> {
                        true
                    }  // Prevents reloading of the current destination
                    else -> it.onNavDestinationSelected(navController)
                            || super.onOptionsItemSelected(it)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
//            Log.d(TAG, "MOVING TO: ${destination.displayName}")

            binding.appBarMainInclude.mainActivityToolbar.apply {
                // Resets any NavigationOnClickListeners for the Up button (e.g. in RootAccountEditFragment)
                setupWithNavController(navController, appBarConfiguration)

                // Visibility
                visibility = when (destination.id) {
                    R.id.titleFragment, R.id.lobbyFragment -> View.GONE
                    else -> View.VISIBLE
                }
            }

            binding.rootDL.apply {
                // DrawerLockMode
                when (destination.id) {
                    R.id.transactionsFragment,
                    R.id.budgetsFragment,
                    R.id.debtFragment,
                    R.id.categoriesFragment,
                    R.id.accountsFragment -> setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    else -> setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }

            binding.appBarMainInclude.mainActivityFAB.apply {
                // FAB Visibility
                when (destination.id) {
                    R.id.transactionsFragment -> show()
                    else -> hide()
                }
            }

        }

        val headerBinding = NavHeaderMainBinding.bind(binding.mainActivityNV.getHeaderView(0))
        mainActivityViewModel.currentUser.observe(this) {
            if (it == null) return@observe
            Log.d(TAG, "currentUser changed: id: ${it.uid}, anonymous? ${it.isAnonymous}")
            if (it.isAnonymous) {
                headerBinding.root.setOnClickListener {
                    startActivityForResult(
                        mainActivityViewModel.loginIntentNoAnonymous(),
                        START_CLAIM_LOGIN_INTENT
                    )
                }
                headerBinding.navHeaderDisplayNameTV.text =
                    getString(R.string.nav_header_guest_account)
                headerBinding.navHeaderEmailTV.text =
                    getString(R.string.nav_header_click_here_to_claim_account)
            } else {
                headerBinding.navHeaderDisplayNameTV.text = it.displayName
                headerBinding.navHeaderEmailTV.text = it.email
            }
        }

        mainActivityViewModel.showOutdatedLoginSnackbar.observe(this)
        {
            if (it) {
                mainActivityViewModel.showOutdatedLoginSnackbarHandled()
                Snackbar.make(
                    binding.root,
                    "Cloud Backup is disabled as there is a newer login on another device.",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setBehavior(NoSwipeBehaviour())
                    .setAction("OK") { Unit }
                    .show()
            }
        }

        WorkManager.getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData(UploadDataWorker.WORK_NAME)
            .observe(this)
            { workInfos ->
                Log.d(TAG, "workInfos.size = ${workInfos.size}")
                workInfos.forEachIndexed { index, workInfo ->
                    Log.d(
                        TAG,
                        "workInfo #$index state is ${workInfo.state.name} with tags ${workInfo.tags}, runAttemptCount ${workInfo.runAttemptCount}, progress ${workInfo.progress}"
                    )
//                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
//                        Snackbar.make(
//                            requireView(),
//                            R.string.work_completed, Snackbar.LENGTH_SHORT
//                        )
//                            .show()
//                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            START_SETTING_ACTIVITY -> if (UserPDS.getString("dsp_theme") != previousLoadedTheme) recreate()
            START_CLAIM_LOGIN_INTENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    mainActivityViewModel.refreshAuthMLD()
                    if (navController.currentDestination?.id !in listOf(
                            R.id.titleFragment,
                            R.id.lobbyFragment,
                            R.id.exitLobbyFragment
                        )
                    ) {
                        // To complete set up
                        navController.navigate(TopLevelNavGraphDirections.actionGlobalTitleFragment())
                    }
                } else {
                    val response = IdpResponse.fromResultIntent(data)
                    when (val code = response?.error?.errorCode) {
                        ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT -> {
                            Snackbar.make(
                                binding.root,
                                "Claim failed. This email address was used for another account.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        ErrorCodes.NO_NETWORK -> {
                            Snackbar.make(
                                binding.root,
                                "Check your network connection.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        ErrorCodes.DEVELOPER_ERROR -> {
                            Snackbar.make(
                                binding.root,
                                "Developer error: Please report this bug.",
                                Snackbar.LENGTH_INDEFINITE
                            ).show()
                        }
                        ErrorCodes.UNKNOWN_ERROR -> {
                            Snackbar.make(
                                binding.root,
                                "Unknown error: Please report this bug.",
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .setBehavior(NoSwipeBehaviour())
                                .show()
                        }
                        null -> {
                            Unit
                        }
                        else -> {
                            Snackbar.make(
                                binding.root,
                                "Unspecified error [$code]: Please report this bug.",
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .setBehavior(NoSwipeBehaviour())
                                .show()
                        }
                    }
                }

                Firebase.auth.currentUser?.let {
                    Log.d(
                        TAG,
                        "uid is now ${it.uid} and last login is ${it.metadata?.lastSignInTimestamp}"
                    )
                }

            }
        }
    }
}
