package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.kaeonx.moneymanager.databinding.FragmentTypeDisplayBinding

// tabLayoutControllerFragment: where the TabLayout is controlled
// retaining instance of fragmentStateAdapter to access the clickListener. A cheap solution that might not be the best. // TODO(FUTURE)
// SOF Article to continue from: https://stackoverflow.com/a/53935427/7254995
class TypeDisplayFragment(private val fragmentStateAdapter: TypeDisplayFragmentStateAdapter) :
    Fragment() {

    private val type by lazy {
        when (val catPickerState = requireArguments().getInt(CAT_PICKER_STATE)) {
            0 -> "Income"
            1 -> "Expenses"
            else -> throw IllegalStateException("Unknown CAT_PICKER_STATE $catPickerState")
        }
    }
    private lateinit var binding: FragmentTypeDisplayBinding
    private val viewModel: TypeDisplayViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTypeDisplayBinding.inflate(inflater, container, false)
        binding.root.adapter = TypeDisplayRVAdapter(
            type,
            requireArguments().getBoolean(CAT_PICKER_EDITABLE),
            fragmentStateAdapter.itemOnClickListener
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.categories.observe(viewLifecycleOwner) { list ->
            if (list == null) return@observe
            (binding.root.adapter as TypeDisplayRVAdapter).submitListAndAddTailIfNecessary(
                list.filter { it.type == type }
            )
        }
    }
}
