package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.databinding.FragmentTypeDisplayBinding
import com.kaeonx.moneymanager.userrepository.domain.Category

private const val TAG = "tpfrag"

// tabLayoutControllerFragment: where the TabLayout is controlled
class TypeDisplayFragment(private val itemOnClickListener: CategoryOnClickListener): Fragment() {

    private lateinit var binding: FragmentTypeDisplayBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    interface TypeDisplayListener {
        fun onCategorySelected(type: String, category: Category)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTypeDisplayBinding.inflate(inflater, container, false)

        val type = when (val catPickerState = requireArguments().getInt(CAT_PICKER_STATE)) {
            0 -> "Income"
            1 -> "Expenses"
            else -> throw IllegalStateException("Unknown CAT_PICKER_STATE $catPickerState")
        }

//        val savedCategories =

        binding.root.adapter = TypeDisplayRVAdapter(
            type,
            requireArguments().getBoolean(CAT_PICKER_EDITABLE),
            itemOnClickListener
        )

        return binding.root
    }

    override fun onStart() {
        Log.d(TAG, "onStart(): called (${requireArguments().getInt(CAT_PICKER_STATE)})")
        super.onStart()
    }

    override fun onStop() {
        Log.d(TAG, "onStop(): called (${requireArguments().getInt(CAT_PICKER_STATE)})")
        super.onStop()
    }

    override fun onResume() {
        Log.d(TAG, "onResume(): called (${requireArguments().getInt(CAT_PICKER_STATE)})")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause(): called (${requireArguments().getInt(CAT_PICKER_STATE)})")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy(): called (${requireArguments().getInt(CAT_PICKER_STATE)})")
        super.onDestroy()
    }
}
