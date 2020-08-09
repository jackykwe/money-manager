package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.databinding.RvLlItemDetailTypeBinding

////////////////////////////////////////////////////////////////////////////////
/**
 * ll_item_detail_type_legend
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("nameTV_typeface")
fun TextView.setNameTVTypeface(name: String) {
    if (name == "(multiple)") {
        text = buildSpannedString { italic { append(name) } }
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_type_categories
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categoriesLL_categoryLLData", "categoriesLL_onClickListener")
fun LinearLayout.setCategoriesLLAdapter(
    list: List<DetailTypeCategoryLLData>,
    itemOnClickListener: DetailTypeOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (typeCategoryLLData in list) {
        val itemBinding = RvLlItemDetailTypeBinding.inflate(layoutInflater, null, false)
        itemBinding.typeCategoryLLData = typeCategoryLLData
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}