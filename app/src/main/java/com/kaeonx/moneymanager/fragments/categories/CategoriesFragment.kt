package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.databinding.FragmentCategoriesBinding
import com.kaeonx.moneymanager.userrepository.UserRepository

private const val TAG = "catFrag"

// Essentially the same code as CategoriesDF, except for the adapter.
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.catPickerVP.offscreenPageLimit = 1
        binding.catPickerVP.adapter =
            TypeDisplayFragmentStateAdapter(this, true,
                CategoryOnClickListener { category ->
                    val cond1 = when (val type = category.type) {
                        "Income" -> UserRepository.getInstance().categories.value!!.count { it.type == "Income" } > 1
                        "Expenses" -> UserRepository.getInstance().categories.value!!.count { it.type == "Expenses" } > 1
                        else -> throw IllegalArgumentException("Unknown type $type")
                    }
                    val cond2 = category.name != "Add…"
                    findNavController().run {
                        if (currentDestination?.id == R.id.categoriesFragment) {
                            navigate(
                                CategoriesFragmentDirections.actionCategoriesFragmentToCategoryEditFragment(
                                    oldCategory = category,
                                    deletable = cond1 && cond2
                                )
                            )
                        }
                    }
                }
            )
//        binding.catPickerVP.setCurrentItem(UserPDS.getString("tst_default_type").let {
//            when (it) {
//                "Income" -> 0
//                "Expenses" -> 1
//                else -> throw IllegalStateException("Unknown type $it")
//            }
//        }, false)
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