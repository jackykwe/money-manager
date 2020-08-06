package com.kaeonx.moneymanager.fragments.type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentTypeDetailBinding

private const val TAG = "exfrag"

class TypeDetailFragment : Fragment() {

    private lateinit var binding: FragmentTypeDetailBinding

    private val args: TypeDetailFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        TypeDetailViewModelFactory(
            args.type,
            args.displayCalendar,
            args.showCurrency
        )
    }
    private val viewModel: TypeDetailViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.title =
            args.type
        binding = FragmentTypeDetailBinding.inflate(inflater, container, false)
        binding.typeRV.apply {
            setHasFixedSize(true)
            adapter = TypeDetailRVAdapter(
                TypeDetailOnClickListener {
                    Toast.makeText(requireContext(), "Oh? You want $it?", Toast.LENGTH_SHORT).show()
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.typeRVPacket.observe(viewLifecycleOwner) {
            (binding.typeRV.adapter as TypeDetailRVAdapter).apply {
                if (it == null) return@observe
//                submitList(null)
                submitList2(it)
            }
        }
    }
}