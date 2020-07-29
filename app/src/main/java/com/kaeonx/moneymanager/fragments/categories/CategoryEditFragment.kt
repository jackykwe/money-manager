package com.kaeonx.moneymanager.fragments.categories

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.adapters.ColourFamilyPickerArrayAdapter
import com.kaeonx.moneymanager.adapters.ColourIntensityPickerArrayAdapter
import com.kaeonx.moneymanager.databinding.FragmentCategoryEditBinding

private const val TAG = "categoriesEditFrag"

class CategoryEditFragment : Fragment() {

    private lateinit var binding : FragmentCategoryEditBinding

    private val args: CategoryEditFragmentArgs by navArgs()
    private val viewModelFactory by lazy { CategoryEditViewModelFactory(args.oldCategory) }
    private val viewModel: CategoryEditViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // Courtesy of https://stackoverflow.com/a/48185496/7254995
        // Disables typing
        binding.colourFamilySpinner.apply {
            inputType = InputType.TYPE_NULL
            setAdapter(ColourFamilyPickerArrayAdapter(arrayListOf(), null))  // Actual updating of adapter is done via data binding
        }

        binding.colourIntensitySpinner.apply {
            inputType = InputType.TYPE_NULL
            setAdapter(ColourIntensityPickerArrayAdapter("Red", arrayListOf()))  // Actual updating of adapter is done via data binding
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.focusClearer.setOnFocusChangeListener { _, focused ->
            if (focused) {
                // Close the keyboard, if it's open
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            }
        }

        viewModel.showSnackBarText.observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.snackBarShown()
            }
        }
        viewModel.navigateUp.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.navigateUpHandled()
                findNavController().navigateUp()
            }
        }
    }

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
                                viewModel.deleteOldCategory()
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

        // This callback will only be called when this fragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            isEnabled = true
            if (viewModel.changesWereMade()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Abandon unsaved changes?")
                    .setPositiveButton(R.string.ok) { _, _ ->  findNavController().navigateUp() }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .create()
                    .show()
            } else {
                findNavController().navigateUp()
            }
        }

        // Sets behaviour of UP button to be the same as BACK button (callback in onBackPressedDispatcher)
        (requireActivity() as MainActivity).binding.appBarMainInclude.mainActivityToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }
}