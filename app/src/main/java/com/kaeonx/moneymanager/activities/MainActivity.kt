package com.kaeonx.moneymanager.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.BuildConfig
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.TopLevelNavGraphDirections
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

// TODO: ADD DIALOG FRAGMENTS TO NAVIGATION
// I'm dealing with all dialogs using the not so new method - supportFragmentManager / childFragmentManager
// and not using Navigation UI for now. Navigation UI is only used for when the main screen changes.
private const val TAG = "matvt"

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        // This must be called before the onCreate. If not, onStart will run twice!
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.transactionsFragment//,
//            R.id.budgetFragment,
//            R.id.debtFragment,
//            R.id.titleFragment
        ), rootDL)

        mainActivityNV.setupWithNavController(navController) // Connects navigation drawer to navController
        mainActivityNV.setNavigationItemSelectedListener {
            rootDL.closeDrawers()
            when (it.itemId) {
                R.id.menuExport -> {
                    Snackbar.make(findViewById(R.id.rootDL), "Function not available yet", Snackbar.LENGTH_SHORT).show()
                    false
                }
                R.id.menuSettings -> {
                    Snackbar.make(findViewById(R.id.rootDL), "Function not available yet", Snackbar.LENGTH_SHORT).show()
//                    startActivity(Intent(this, SettingsActivity::class.java))
//                    startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
                    false
                }
                R.id.menuLogout -> {
                    authViewModel.logout()
                    false
                }
                R.id.menuAbout -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage("Version ${BuildConfig.VERSION_NAME}")
                        .create()
                        .show()
                    false
                }
                navController.currentDestination!!.id -> { true }  // Prevents reloading of the current destination
                else -> it.onNavDestinationSelected(navController) || super.onOptionsItemSelected(it)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "MOVING TO: ${destination.displayName}")

            // Inflation of menu is done in each fragment
            // (because the options need to be controlled from within the fragment)
            mainActivityToolbar.menu.clear()
            // Resets any NavigationOnClickListeners for the Up button (e.g. in RootAccountEditFragment)
            mainActivityToolbar.setupWithNavController(navController, appBarConfiguration)

            // AppBar Visibility
            mainActivityToolbar.visibility = when (destination.id) {
                R.id.titleFragment ->  View.GONE
                else -> View.VISIBLE
            }

            // DrawerLockMode
            when (destination.id) {
                R.id.transactionsFragment,
                R.id.BudgetFragment,
                R.id.debtFragment,
                R.id.categoriesFragment,
                R.id.accountsFragment -> rootDL.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                else -> rootDL.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            // FAB Visibility
            when (destination.id) {
                R.id.transactionsFragment -> mainActivityFAB.show()
                else -> mainActivityFAB.hide()
            }

            // CatPickerTLExtendedAppBar Visibility
            catPickerTLExtendedAppBar.visibility = when (destination.id) {
                R.id.categoriesFragment -> View.VISIBLE
                else -> View.GONE
            }
        }

        authViewModel.currentUser.observe(this) {
            if (it == null) {
                // Logout logic. Login logic is controlled from within RootTitleFragment.
                navController.navigate(TopLevelNavGraphDirections.actionGlobalTitleFragment())
                mainActivityNV.getHeaderView(0).navHeaderDisplayNameTV.text = null
                mainActivityNV.getHeaderView(0).navHeaderEmailTV.text = null
            } else {
                mainActivityNV.getHeaderView(0).navHeaderDisplayNameTV.text = it.displayName
                mainActivityNV.getHeaderView(0).navHeaderEmailTV.text = it.email
//                // Needed for preferences to work
//                PreferenceDS.loadUid(it.uid)
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        // todo: This solution is very hacky.. Don't really get how it actually works, but it fits my demands perfectly. research?
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
//            recreate()  // some settings were set that needs refreshing of this activity.
//        }
//    }
}
