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
import com.kaeonx.moneymanager.customclasses.GenericOnClickListener
import com.kaeonx.moneymanager.databinding.FragmentDetailTypeBinding

class DetailTypeFragment : Fragment() {

    private lateinit var binding: FragmentDetailTypeBinding

    private val args: DetailTypeFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        DetailTypeViewModelFactory(
            args.initType,
            args.initCalendar,
            args.showCurrency
        )
    }
    private val typeViewModel: DetailTypeViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.title =
//            args.type
        binding = FragmentDetailTypeBinding.inflate(inflater, container, false)
        binding.typeRV.apply {
            setHasFixedSize(true)
            adapter = DetailTypeRVAdapter(
                DetailTypeOnClickListener {
                    Toast.makeText(requireContext(), "Oh? You want $it?", Toast.LENGTH_SHORT).show()
                },
                GenericOnClickListener {
                    typeViewModel.swapType()
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        typeViewModel.type.observe(viewLifecycleOwner) {
            (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.title =
                it
        }

        typeViewModel.detailTypeRVPacket.observe(viewLifecycleOwner) {
            (binding.typeRV.adapter as DetailTypeRVAdapter).apply {
                if (it == null) return@observe
//                submitList(null)
                submitList2(it)
            }
        }
    }
}