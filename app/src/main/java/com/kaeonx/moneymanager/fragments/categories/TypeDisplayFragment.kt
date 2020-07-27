package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.kaeonx.moneymanager.activities.AuthViewModel
import com.kaeonx.moneymanager.databinding.FragmentTypeDisplayBinding

// tabLayoutControllerFragment: where the TabLayout is controlled
class TypeDisplayFragment : Fragment() {

    private val type by lazy {
        when (val catPickerState = requireArguments().getInt(CAT_PICKER_STATE)) {
            0 -> "Income"
            1 -> "Expenses"
            else -> throw IllegalStateException("Unknown CAT_PICKER_STATE $catPickerState")
        }
    }
    private lateinit var binding: FragmentTypeDisplayBinding
    private val authViewModel: AuthViewModel by activityViewModels()
    private val viewModelFactory by lazy { TypeDisplayViewModelFactory(authViewModel.currentUser.value!!.uid, type) }
    private val viewModel: TypeDisplayViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTypeDisplayBinding.inflate(inflater, container, false)
//        val savedCategories =

        binding.root.adapter = TypeDisplayRVAdapter(
            type,
            requireArguments().getBoolean(CAT_PICKER_EDITABLE),
            requireArguments().getSerializable(CAT_PICKER_LISTENER) as CategoryOnClickListener
        )

        viewModel.categories.observe(viewLifecycleOwner) {
            (binding.root.adapter as TypeDisplayRVAdapter).submitListAndAddTailIfNecessary(it)
        }

        return binding.root
    }
}
