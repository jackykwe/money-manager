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
import com.kaeonx.moneymanager.databinding.ActivityMainBinding
import com.kaeonx.moneymanager.databinding.NavHeaderMainBinding
import com.kaeonx.moneymanager.txnrepository.database.UserDatabase

// TODO: ADD DIALOG FRAGMENTS TO NAVIGATION
// I'm dealing with all dialogs using the not so new method - supportFragmentManager / childFragmentManager
// and not using Navigation UI for now. Navigation UI is only used for when the main screen changes.
private const val TAG = "matvt"

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    internal lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        // This must be called before the onCreate. If not, onStart will run twice!
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.transactionsFragment//,
//            R.id.budgetFragment,
//            R.id.debtFragment,
//            R.id.titleFragment
        ), binding.rootDL)

        binding.mainActivityNV.apply {
            setupWithNavController(navController) // Connects navigation drawer to navController
            setNavigationItemSelectedListener {
                binding.rootDL.closeDrawers()
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
                        AlertDialog.Builder(this@MainActivity)
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
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "MOVING TO: ${destination.displayName}")

            binding.appBarMainInclude.mainActivityToolbar.apply {
                // Inflation of menu is done in each fragment
                // (because the options need to be controlled from within the fragment)
                menu.clear()
                // Resets any NavigationOnClickListeners for the Up button (e.g. in RootAccountEditFragment)
                setupWithNavController(navController, appBarConfiguration)
                // Visibility
                visibility = when (destination.id) {
                    R.id.titleFragment ->  View.GONE
                    else -> View.VISIBLE
                }
            }

            binding.rootDL.apply {
                // DrawerLockMode
                when (destination.id) {
                    R.id.transactionsFragment,
                    R.id.BudgetFragment,
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

            binding.appBarMainInclude.catPickerTLExtendedAppBar.apply {
                // CatPickerTLExtendedAppBar Visibility
                visibility = when (destination.id) {
                    R.id.categoriesFragment -> View.VISIBLE
                    else -> View.GONE
                }
            }
        }

        val headerBinding = NavHeaderMainBinding.bind(binding.mainActivityNV.getHeaderView(0))
        headerBinding.lifecycleOwner = this
        headerBinding.authViewModel = authViewModel
        authViewModel.currentUser.observe(this) {
            if (it == null) {
                // Logout logic. Login logic is controlled from within RootTitleFragment.
                navController.navigate(TopLevelNavGraphDirections.actionGlobalTitleFragment())
                UserDatabase.dropInstance()
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
