package com.kaeonx.moneymanager.fragments.title

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kaeonx.moneymanager.databinding.FragmentLobbyBinding
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LobbyFragment : Fragment() {

    private lateinit var binding: FragmentLobbyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //TODO : CHANGE ALL COROUTINESCOPE.LAUNCH() TO LIFECYCLESCOPE.LAUNCH()
        // todo: remove all data from transactionsfragment, then see if the inflation still causes lag.
        // todo: if it does, it isn't the data's fault.
        lifecycleScope.launch(Dispatchers.Main) {
            val userRepository = UserRepository.getInstance()
            delay(200L)
            val xeRepository = XERepository.getInstance()
            while (userRepository.accounts.value == null ||
                userRepository.categories.value == null ||
                userRepository.preferences.value == null ||
                xeRepository.xeRows.value == null
            ) {
                Log.d("Lobby", "still waiting sir")
                delay(200L)
            }
            findNavController().navigate(LobbyFragmentDirections.actionLobbyFragmentToTransactionsFragment())
        }
    }
}