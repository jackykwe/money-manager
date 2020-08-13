package com.kaeonx.moneymanager.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.github.mikephil.charting.utils.Utils
import com.kaeonx.moneymanager.BuildConfig
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.TopLevelNavGraphDirections
import com.kaeonx.moneymanager.databinding.ActivityMainBinding
import com.kaeonx.moneymanager.databinding.NavHeaderMainBinding
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.xerepository.XERepository

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    internal lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        // This must be called before the onCreate. If not, onStart will run twice!
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
            setupWithNavController(navController) // Connects navigation drawer to navController
            setNavigationItemSelectedListener {
                binding.rootDL.closeDrawers()
                when (it.itemId) {
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
                    else -> it.onNavDestinationSelected(navController)
                            || super.onOptionsItemSelected(it)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
//            Log.d(TAG, "MOVING TO: ${destination.displayName}")
            // Close the keyboard, if it's open
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            binding.appBarMainInclude.mainActivityToolbar.apply {
                // Inflation of menu is done here for these special pairs (interacting with DFs via NavUI).
                // Otherwise, inflation of menu is done set in each fragment
                // (because the options need to be controlled from within the fragment)
                when (destination.id) {
                    R.id.transactionsBSDF -> Unit  // Pair 1, 2
                    R.id.transactionsSearchFragment -> Unit
                    else -> menu.clear()
                }
                when (destination.id) {
                    R.id.transactionsFragment -> inflateMenu(R.menu.fragment_transactions)  // Pair 1
                    R.id.transactionEditFragment -> inflateMenu(R.menu.fragment_general_edit_deleteable)  // Pair 2
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
