package com.kaeonx.moneymanager.fragments.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentDetailCategoryBinding

class DetailCategoryFragment : Fragment() {

    private lateinit var binding: FragmentDetailCategoryBinding

    private val args: DetailCategoryFragmentArgs by navArgs()
    private val viewModelFactory by lazy {
        DetailCategoryViewModelFactory(
            args.type,
            args.category,
            args.calendarStart,
            args.calendarEnd
        )
    }
    private val viewModel: DetailCategoryViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.title =
            "${args.type}: ${args.category}"
        binding = FragmentDetailCategoryBinding.inflate(inflater, container, false)
        binding.detailCategoryRV.apply {
            setHasFixedSize(true)
            adapter = DetailCategoryRVAdapter(
                DetailCategoryOnClickListener { transactionId ->
                    findNavController().run {
                        if (currentDestination?.id == R.id.detailCategoryFragment) {
                            navigate(
                                DetailCategoryFragmentDirections.actionDetailCategoryFragmentToTransactionEditFragment(
                                    transactionId
                                )
                            )
                        }
                    }
//                    Toast.makeText(
//                        requireContext(),
//                        "Oh? You want $transactionId?",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.categoryTypeRVPacket.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            (binding.detailCategoryRV.adapter as DetailCategoryRVAdapter).apply {
//                submitList(null)
                submitList2(it)
            }
        }
    }
}