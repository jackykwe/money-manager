package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentCategoriesBinding
import com.kaeonx.moneymanager.userrepository.UserRepository

private const val TAG = "catFrag"

// The counterpart to this Fragment is CategoriesDF
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding

    private fun enableCatPickerVPFadeAnimation() {
        // Courtesy of https://stackoverflow.com/a/56310461/7254995
        binding.catPickerVP.setPageTransformer { page, _ ->
            page.apply {
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(
                        page.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                    )
                    .setListener(null)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.catPickerVP.offscreenPageLimit = 1
//        enableCatPickerVPFadeAnimation()
        binding.catPickerVP.adapter =
            TypeDisplayFragmentStateAdapter(this, true,
                CategoryOnClickListener { category ->
                    val cond1 = when (val type = category.type) {
                        "Income" -> UserRepository.getInstance().categories.value!!.count { it.type == "Income" } > 1
                        "Expenses" -> UserRepository.getInstance().categories.value!!.count { it.type == "Expenses" } > 1
                        else -> throw IllegalArgumentException("Unknown type $type")
                    }
                    val cond2 = category.name != "Add..."
                    findNavController().run {
                        if (currentDestination?.id == R.id.categoriesFragment) {
                            navigate(
                                CategoriesFragmentDirections.actionCategoriesFragmentToCategoryEditFragment(
                                    category,
                                    cond1 && cond2
                                )
                            )
                        }
                    }
                }
            )
//        binding.catPickerVP.setCurrentItem(1, false) //todo: bind to default
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //        TabLayoutMediator(view.catPickerTL, view.catPickerVP) { tab, position ->
        TabLayoutMediator(
            (requireActivity() as MainActivity).binding.appBarMainInclude.catPickerTLExtendedAppBar,
            binding.catPickerVP
        ) { tab, position ->
            tab.text = when (position) {
                0 -> "Income"
                1 -> "Expenses"
                else -> throw IllegalArgumentException("Unknown tab position ($position) reached. Position should be 0 or 1 only.")
            }
        }.attach()
    }
}