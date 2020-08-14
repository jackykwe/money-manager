package com.kaeonx.moneymanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.databinding.DropdownItemColourSpinnerBinding
import com.kaeonx.moneymanager.handlers.ColourHandler

// Inspiration from https://www.youtube.com/watch?v=GeO5F0nnzAw and https://www.youtube.com/watch?v=ocM1Yw_ktqM

class ColourFamilyPickerArrayAdapter :
    ArrayAdapter<String>(App.context, 0, ColourHandler.getColourFamilies()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            DropdownItemColourSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            DropdownItemColourSpinnerBinding.bind(convertView)
        }

        val colourFamily =
            getItem(position) ?: throw IllegalStateException("getItem(position) returned null")
        binding.colourTV.text = colourFamily
        binding.colourDisplayIV.drawable.setTintList(
            ColourHandler.getColorStateListOf(colourFamily)
        )

        return binding.root
    }
}