package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.FragmentCategoriesBinding

private const val TAG = "catFrag"

// The counterpart to this Fragment is CategoriesDF
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding

    private fun enableCatPickerVPAnimation() {
        // Courtesy of https://stackoverflow.com/a/56310461/7254995
        binding.catPickerVP.setPageTransformer { page, _ ->
            page.apply {
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(page.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                    .setListener(null)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.catPickerVP.offscreenPageLimit = 1
//        enableCatPickerVPAnimation()
        binding.catPickerVP.adapter = TypeDisplayFragmentStateAdapter(this, true, CategoryOnClickListener { category ->
            Toast.makeText(requireContext(), "Oh? You want $category?", Toast.LENGTH_SHORT).show()
//            findNavController().navigate(
//                CategoriesFragmentDirections.actionRootCategoriesFragmentToRootCategoryEditFragment(
//                    type,
//                    category
//                )
//            )
        })
//        binding.catPickerVP.setCurrentItem(1, false) //todo: bind to default
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //        TabLayoutMediator(view.catPickerTL, view.catPickerVP) { tab, position ->
        TabLayoutMediator((requireActivity() as MainActivity).binding.appBarMainInclude.catPickerTLExtendedAppBar, binding.catPickerVP) { tab, position ->
            tab.text = when (position) {
                0 -> "Income"
                1 -> "Expenses"
                else -> throw IllegalArgumentException("Unknown tab position ($position) reached. Position should be 0 or 1 only.")
            }
        }.attach()
    }
}