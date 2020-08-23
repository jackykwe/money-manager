package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.databinding.FragmentCategoriesBinding
import com.kaeonx.moneymanager.userrepository.UserPDS

internal const val CATEGORIES_DF_RESULT = "categories_df_result"

// Essentially the same code as CategoriesFragment, except for the adapter.
class CategoriesDF : DialogFragment() {

    private lateinit var binding: FragmentCategoriesBinding

    // TODO: Enable on touch outside dismiss
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.catPickerVP.offscreenPageLimit = 1
        binding.catPickerVP.adapter =
            TypeDisplayFragmentStateAdapter(
                parentFragment = this,
                editable = false,
                itemOnClickListener = CategoryOnClickListener { _, category ->
                    findNavController().getBackStackEntry(R.id.transactionsBSDF).savedStateHandle.set(
                        CATEGORIES_DF_RESULT,
                        category
                    )
                    findNavController().navigateUp()
                },
                itemOnLongClickListener = CategoryOnClickListener { _, _ -> Unit }
            )
        binding.catPickerVP.setCurrentItem(UserPDS.getString("tst_default_type").let {
            when (it) {
                "Income" -> 0
                "Expenses" -> 1
                else -> throw IllegalStateException("Unknown type $it")
            }
        }, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        TabLayoutMediator(binding.catPickerTL, binding.catPickerVP) { tab, position ->
            tab.text = when (position) {
                0 -> "Income"
                1 -> "Expenses"
                else -> throw IllegalArgumentException("Unknown tab position ($position) reached. Position should be 0 or 1 only.")
            }
        }.attach()
    }
}