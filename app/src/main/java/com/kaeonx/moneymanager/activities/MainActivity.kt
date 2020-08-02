package com.kaeonx.moneymanager.activities

import android.annotation.SuppressLint
import android.content.Intent
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
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.xerepository.XERepository

// TODO: ADD DIALOG FRAGMENTS TO NAVIGATION
// I'm dealing with all dialogs using the not so new method - supportFragmentManager / childFragmentManager
// and not using Navigation UI for now. Navigation UI is only used for when the fragment_transactions screen changes.
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
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.transactionsFragment//,
//            R.id.budgetFragment,
//            R.id.debtFragment,
//            R.id.titleFragment
            ), binding.rootDL
        )

        binding.mainActivityNV.apply {
            setupWithNavController(navController) // Connects navigation drawer to navController
            setNavigationItemSelectedListener {
                binding.rootDL.closeDrawers()
                when (it.itemId) {
                    R.id.menuExport -> {
                        Snackbar.make(
                            binding.appBarMainInclude.mainActivityFAB,
                            "Function not available yet",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        false
                    }
                    R.id.menuSettings -> {
//                        Snackbar.make(binding.appBarMainInclude.mainActivityFAB, "Function not available yet", Snackbar.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
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
                    navController.currentDestination!!.id -> {
                        true
                    }  // Prevents reloading of the current destination
                    else -> it.onNavDestinationSelected(navController) || super.onOptionsItemSelected(
                        it
                    )
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "MOVING TO: ${destination.displayName}")

            binding.appBarMainInclude.mainActivityToolbar.apply {
                // Inflation of menu is done here for these special pairs (interacting with DFs via NavUI).
                // Otherwise, inflation of menu is done set in each fragment
                // (because the options need to be controlled from within the fragment)
                when (destination.id) {
                    R.id.transactionsBSDF -> Unit  // Pair 1
                    R.id.monthYearPickerDialogFragment -> Unit  // Pair 2
                    else -> menu.clear()
                }
                when (destination.id) {
                    R.id.transactionEditFragment -> inflateMenu(R.menu.fragment_general_edit_deleteable)  // Pair 1
                    R.id.transactionsFragment -> inflateMenu(R.menu.fragment_transactions)  // Pair 2
                    else -> Unit
                }

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
                UserRepository.dropInstance()
                UserDatabase.dropInstance()
                XERepository.dropInstance()
            }
        }
    }
}
