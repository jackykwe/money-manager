package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentCategoriesBinding

private const val TAG = "catFrag"

// The counterpart to this Fragment is CategoryPickerDF
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        binding.catPickerVP.adapter = TypeDisplayFragmentStateAdapter(this, true, CategoryOnClickListener { type, category ->
            Toast.makeText(requireContext(), "Oh? You want ${category.name}?", Toast.LENGTH_SHORT).show()
//            findNavController().navigate(
//                CategoriesFragmentDirections.actionRootCategoriesFragmentToRootCategoryEditFragment(
//                    type,
//                    category
//                )
//            )
        })
//        binding.catPickerVP.setCurrentItem(1, false)

        //        TabLayoutMediator(view.catPickerTL, view.catPickerVP) { tab, position ->
        TabLayoutMediator((requireActivity() as MainActivity).binding.appBarMainInclude.catPickerTLExtendedAppBar, binding.catPickerVP) { tab, position ->
            tab.text = when (position) {
                0 -> "Income"
                1 -> "Expenses"
                else -> throw IllegalArgumentException("Unknown tab position ($position) reached. Position should be 0 or 1 only.")
            }
        }.attach()

        return binding.root
    }

    override fun onStart() {
        Log.d(TAG, "onStart(): called")
        super.onStart()
    }

    override fun onStop() {
        Log.d(TAG, "onStop(): called")
        super.onStop()
    }

    override fun onResume() {
        Log.d(TAG, "onResume(): called")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause(): called")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy(): called")
        super.onDestroy()
    }
}