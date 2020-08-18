package com.kaeonx.moneymanager.fragments.title

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentLobbyBinding
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

private const val TAG = "lobbyfrag"

class LobbyFragment : Fragment() {

    private lateinit var binding: FragmentLobbyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.menu.clear()

        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Main) {
            val xeRepository = XERepository.getInstance()
            val userRepository = UserRepository.getInstance()
            while (
                userRepository.accounts.value == null ||
                userRepository.categories.value == null ||
                userRepository.preferences.value == null ||
                xeRepository.xeRows.value == null
            ) {
                ensureActive()
//                Log.d("Lobby", "still waiting sir")
                delay(1L)
            }

            UserPDS.putDSPString("logged_in_uid", Firebase.auth.currentUser!!.uid)

            // Ensure theme is correct
            val userTheme = UserPDS.getString("dsp_theme").also { Log.d(TAG, "userTheme is $it") }
            val sharedPrefTheme = UserPDS.getDSPString("dsp_theme", "light")
                .also { Log.d(TAG, "sharedPrefTheme is $it") }
            if (sharedPrefTheme != userTheme) {
                UserPDS.putDSPString("dsp_theme", UserPDS.getString("dsp_theme"))
                Snackbar.make(requireView(), "Applying theme…", Snackbar.LENGTH_SHORT).show()
                delay(1000L)
                requireActivity().recreate()
            }

            val animDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            binding.root.animate()
                .alpha(0f)
                .setDuration(animDuration)
                .setListener(null)
            delay(animDuration + 50)
            findNavController().run {
                if (currentDestination?.id == R.id.lobbyFragment) {
                    navigate(LobbyFragmentDirections.actionLobbyFragmentToTransactionsFragment())
                }
            }
        }
    }
}