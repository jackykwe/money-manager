package com.kaeonx.moneymanager.fragments.categories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.customclasses.fixCursorFocusProblems
import com.kaeonx.moneymanager.databinding.FragmentCategoryEditBinding

// TODO: MUST HAVE AT LEAST 1 CATEGORY LEFT, FOR BOTH INCOME AND EXPENSES

private const val TAG = "categoriesEditFrag"

class CategoryEditFragment : Fragment() {

    private lateinit var binding : FragmentCategoryEditBinding

    private val args: CategoryEditFragmentArgs by navArgs()
    private val viewModelFactory by lazy { CategoryEditViewModelFactory(args.oldCategory) }
    private val viewModel: CategoryEditViewModel by viewModels { viewModelFactory }

    private var familyFirstRun = true
    private var intensityFirstRun = true


//    private fun currentColourFamily(): String {
//        return ColourHandler.readColourFamily(currentCategory.colourString)
//    }
//
//    private fun currentColourIntensity(): String? {
//        return ColourHandler.readColourIntensity(currentCategory.colourString)
//    }
//
//    private fun updateALs() {
//        currentColourIntensitiesAL = when (currentColourFamily()) {
//            "Blue Grey", "Brown", "Grey" -> ColourHandler.getColourIntensitiesPartial()
//            else -> ColourHandler.getColourIntensitiesAll()
//        }
//        currentColourFamiliesAL = when (currentColourIntensity()) {
//            "A100", "A200", "A400", "A700" -> ColourHandler.getColourFamiliesWithFullIntensityRange(false)
//            else -> ColourHandler.getColourFamiliesAll(false)
//        }
//    }
//
//    private fun updateSpinners() {
//        colourFamilySpinner.apply{
//            (adapter as ColourFamilyPickerArrayAdapter).updateData(currentColourFamiliesAL, currentColourIntensity())
//            setSelection(currentColourFamiliesAL.indexOf(currentColourFamily()))
//        }
//        colourIntensitySpinner.apply {
//            (adapter as ColourIntensityPickerArrayAdapter).updateData(currentColourFamily(), currentColourIntensitiesAL)
//            setSelection(currentColourIntensitiesAL.indexOf(currentColourIntensity()))
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ETs
//        binding.categoryNameET.fixCursorFocusProblems()
        binding.iconHexET.apply {
//            if (mode == Mode.EDIT) setText(currentCategory.iconHex)
//            doOnTextChanged { text, _, _, _ ->
//                if (text!!.contains(Regex("^F[A-F0-9]{4}$")) && CategoryIconHandler.hexToInt(text.toString()) in 1..5348) {
//                    iconTV.text = CategoryIconHandler.hexToIcon(text.toString())
//                    currentCategory.iconHex = text.toString()
//                } else {
//                    iconTV.text = CategoryIconHandler.hexToIcon("F02D6")  // this will be treated as an error and hence disables saving of edits
//                }
//            }
//            doAfterTextChanged {
//                if (it.isNullOrBlank()) {
//                    it?.append("F")
//                }
//            }
            fixCursorFocusProblems()
        }

//        // Set up icon
//        binding.categoryIconFLInclude.iconRing.visibility = View.INVISIBLE
//        binding.categoryIconFLInclude.iconTV.text = when (mode) {
//            Mode.NEW -> CategoryIconHandler.hexToIcon("F02D6")
//            Mode.EDIT -> CategoryIconHandler.hexToIcon(currentCategory.iconHex)
//        }
//        binding.categoryIconFLInclude.iconBG.drawable.setTint(ColourHandler.getColourObject(resources, currentCategory.colourString))

//        // Set up spinners (initialise)
//        updateALs()
//        val colourFamilyArrayAdapter = ColourFamilyPickerArrayAdapter(
//            requireContext(),
//            resources,
//            currentColourFamiliesAL,
//            currentColourIntensity()
//        )
//        val colourIntensityPickerArrayAdapter = ColourIntensityPickerArrayAdapter(
//            requireContext(),
//            resources,
//            currentColourFamily(),
//            currentColourIntensitiesAL
//        )
//        colourFamilySpinner.apply {
//            adapter = colourFamilyArrayAdapter
//            setSelection(currentColourFamiliesAL.indexOf(currentColourFamily()))
//            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) { }
//                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                    if (!familyFirstRun) {
//                        currentCategory.colourString = ColourHandler.saveColourString(currentColourFamiliesAL[position], currentColourIntensity())
//                        this@RootCategoryEditFragment.iconBG.drawable.setTint(ColourHandler.getColourObject(resources, currentCategory.colourString))
//                        updateALs()
//                        updateSpinners()
//                    } else {
//                        familyFirstRun = !familyFirstRun
//                    }
//                }
//            }
//        }
//        colourIntensitySpinner.apply {
//            adapter = colourIntensityPickerArrayAdapter
//            setSelection(currentColourIntensitiesAL.indexOf(currentColourIntensity()))
//            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) { }
//                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                    if (!intensityFirstRun) {
//                        currentCategory.colourString = ColourHandler.saveColourString(currentColourFamily(), currentColourIntensitiesAL[position])
//                        this@RootCategoryEditFragment.iconBG.drawable.setTint(ColourHandler.getColourObject(resources, currentCategory.colourString))
//                        updateALs()
//                        updateSpinners()
//                    } else {
//                        intensityFirstRun = !intensityFirstRun
//                    }
//                }
//            }
//        }
    }

//    private fun newNameIsDuplicate(): Boolean {
//        return CategoryIconHandler.getCategoryNames(
//            requireContext(),
//            firebaseViewModel.currentUserLD.value!!.uid,
//            oldAndCurrentType,
//            oldCategory.name
//        ).contains(currentCategory.name)
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.apply {
            inflateMenu(R.menu.fragment_accountcategory_edit_default)
            if (args.deleteable) inflateMenu(R.menu.fragment_accountcategory_edit_deleteable)

            setOnMenuItemClickListener {
                // Close the keyboard, if it's open
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                when (it.itemId) {
                    R.id.app_bar_delete -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete category \"${viewModel.currentCategory.value!!.name}\"?")
                            .setMessage("The transactions under \"${viewModel.currentCategory.value!!.name}\" will not be deleted.")
                            .setPositiveButton(R.string.ok) { _, _ ->
                                // CONNECT TO REPO AND DELETE
                                findNavController().navigateUp()
                            }
                            .setNegativeButton(R.string.cancel) { _, _ -> }
                            .create()
                            .show()
                        true
                    }
                    R.id.app_bar_save -> {
                        viewModel.saveBTClicked()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }



//        // This callback will only be called when this fragment is at least Started.
//        requireActivity().onBackPressedDispatcher.addCallback(this) {
//            isEnabled = true
//            when (mode) {
//                Mode.NEW -> {
//                    if (!currentCategory.name.isBlank()
//                        || iconHexET.text.toString() != "F"
//                        || currentColourFamily() != ColourHandler.readColourFamily(oldCategory.colourString)
//                        || currentColourIntensity() != ColourHandler.readColourIntensity(oldCategory.colourString)) {
//                        AlertDialog.Builder(requireContext())
//                            .setMessage("Abandon unsaved changes?")
//                            .setPositiveButton(R.string.ok) { _, _ ->  findNavController().navigateUp() }
//                            .setNegativeButton(R.string.cancel) { _, _ -> }
//                            .create()
//                            .show()
//                    } else {
//                        findNavController().navigateUp()
//                    }
//                }
//                Mode.EDIT -> {
//                    if (currentCategory.name != oldCategory.name
//                        || iconHexET.text.toString() != oldCategory.iconHex
//                        || currentColourFamily() != ColourHandler.readColourFamily(oldCategory.colourString)
//                        || currentColourIntensity() != ColourHandler.readColourIntensity(oldCategory.colourString)) {
//                        AlertDialog.Builder(requireContext())
//                            .setMessage("Abandon unsaved changes?")
//                            .setPositiveButton(R.string.ok) { _, _ -> findNavController().navigateUp() }
//                            .setNegativeButton(R.string.cancel) { _, _ -> }
//                            .create()
//                            .show()
//                    } else {
//                        findNavController().navigateUp()
//                    }
//
//                }
//            }
//        }

//        // Sets behaviour of UP button to be the same as BACK button (callback in onBackPressedDispatcher)
//        requireActivity().mainActivityToolbar.setNavigationOnClickListener {
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }
//
    }
}