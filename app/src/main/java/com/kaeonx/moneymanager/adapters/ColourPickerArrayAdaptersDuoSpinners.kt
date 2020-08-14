package com.kaeonx.moneymanager.adapters

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import com.kaeonx.moneymanager.activities.App
//import com.kaeonx.moneymanager.databinding.DropdownItemColourSpinnerBinding
//import com.kaeonx.moneymanager.handlers.ColourHandler
//
//// Inspiration from https://www.youtube.com/watch?v=GeO5F0nnzAw and https://www.youtube.com/watch?v=ocM1Yw_ktqM
//
//class ColourFamilyPickerArrayAdapterDuoSpinners(var colourFamilies: List<String>, var colourIntensity: String?)
//    : ArrayAdapter<String>(App.context, 0, colourFamilies) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val binding = if (convertView == null) {
//            DropdownItemColourSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
//        } else {
//            DropdownItemColourSpinnerBinding.bind(convertView)
//        }
//
//        val colourFamily = getItem(position)!! // ?: "Black"
//        binding.colourTV.text = colourFamily
//        binding.colourDisplayIV.drawable.setTintList(
//            ColourHandler.getColorStateListOf(
//                colourFamily,
//                colourIntensity ?: "500"
//            )
//        )
//
//        return binding.root
//    }
//
//    fun updateData(newColourFamilies: List<String>, newColourIntensity: String?) {
//        clear()
//        addAll(newColourFamilies)
//        colourIntensity = newColourIntensity
//        notifyDataSetChanged()
//    }
//}
//
//class ColourIntensityPickerArrayAdapterDuoSpinners(var colourFamily: String, var colourIntensities: List<String>)
//    : ArrayAdapter<String>(App.context, 0, colourIntensities) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val binding = if (convertView == null) {
//            DropdownItemColourSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
//        } else {
//            DropdownItemColourSpinnerBinding.bind(convertView)
//        }
//
//        val colourIntensity = getItem(position)!! // ?: "WAIT WHAT TO PUT HERE" // TODO
//        binding.colourTV.text = colourIntensity
//        binding.colourDisplayIV.drawable.setTintList(
//            ColourHandler.getColorStateListOf(
//                colourFamily,
//                colourIntensity
//            )
//        )
//
//        return binding.root
//    }
//
//    fun updateData(newColourFamily: String, newColourIntensities: List<String>) {
//        clear()
//        addAll(newColourIntensities)
//        colourFamily = newColourFamily
//        notifyDataSetChanged()
//    }
//}