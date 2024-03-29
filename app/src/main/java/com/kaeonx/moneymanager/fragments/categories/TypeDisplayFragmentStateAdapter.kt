package com.kaeonx.moneymanager.fragments.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

internal const val CAT_PICKER_STATE = "state"
internal const val CAT_PICKER_EDITABLE = "editable"

// tabLayoutControllerFragment: where the TabLayout is controlled
class TypeDisplayFragmentStateAdapter(
    parentFragment: Fragment,
    private val editable: Boolean,
    internal val itemOnClickListener: CategoryOnClickListener,
    internal val itemOnLongClickListener: CategoryOnClickListener
) : FragmentStateAdapter(parentFragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = TypeDisplayFragment(this)
        fragment.arguments = Bundle().apply {
            putInt(CAT_PICKER_STATE, position)
            putBoolean(CAT_PICKER_EDITABLE, editable)
        }
        return fragment
    }
}