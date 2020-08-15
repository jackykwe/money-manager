package com.kaeonx.moneymanager.fragments.title

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentLobbyBinding
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.UserDatabase
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExitLobbyFragment : Fragment() {

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
            delay(5000L)
            UserRepository.dropInstance()
            UserDatabase.dropInstance()
            XERepository.dropInstance()
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