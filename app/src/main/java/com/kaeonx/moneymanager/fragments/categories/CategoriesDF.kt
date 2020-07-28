package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.databinding.DialogFragmentCategoryPickerBinding

// This DialogFragment is used exclusively with TransactionsBSDF.
// The counterpart to this DialogFragment is CategoriesFragment
// This is the fragment with the Tabs and ViewPager.
class CategoriesDF : DialogFragment() {

    private lateinit var binding: DialogFragmentCategoryPickerBinding

    // TODO: Enable on touch outside dismiss
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFragmentCategoryPickerBinding.inflate(inflater, container, false)
        binding.catPickerVP.offscreenPageLimit = 1
        binding.catPickerVP.adapter = TypeDisplayFragmentStateAdapter(this, false, CategoryOnClickListener { category ->
            findNavController().getBackStackEntry(R.id.transactionsBSDF).savedStateHandle.set("categories_df_result", category)
            findNavController().navigateUp()
        })
//        binding.catPickerVP.setCurrentItem(1, false) //todo: bind to default
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         TabLayoutMediator(binding.catPickerTL, binding.catPickerVP) { tab, position ->
            when (position) {
                0 -> { tab.text = "Income" }
                1 -> { tab.text = "Expenses" }
                else -> throw IllegalArgumentException("Unknown tab position ($position) reached. Position should be 0 or 1 only.")
            }
        }.attach()
    }
}