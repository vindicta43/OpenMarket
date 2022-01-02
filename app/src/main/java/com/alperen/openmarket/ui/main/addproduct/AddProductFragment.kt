package com.alperen.openmarket.ui.main.addproduct

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentAddProductBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.ByteArrayOutputStream

const val GALLERY_PICK = 0
const val CAMERA_PICK = 1

class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val loading by lazy { LoadingFragment() }
    private val imageList = arrayListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)
        binding = FragmentAddProductBinding.inflate(inflater)

        with(binding) {

            return root
        }
    }

    override fun onResume() {
        super.onResume()
        setListenersForText(binding)

        with(binding) {
            with(expandable.secondLayout) {
                val spnCategory = findViewById<Spinner>(R.id.spnCategory)
                val layoutSize = findViewById<ConstraintLayout>(R.id.layoutSize)
                val layoutCondition = findViewById<ConstraintLayout>(R.id.layoutCondition)
                val tvSize = findViewById<TextView>(R.id.tvSize)
                val etSize = findViewById<EditText>(R.id.etSize)
                val spnCondition = findViewById<Spinner>(R.id.spnCondition)
                val male = findViewById<RadioButton>(R.id.btnMale)
                val female = findViewById<RadioButton>(R.id.btnFemale)
                val unisex = findViewById<RadioButton>(R.id.btnUnisex)

                val productName = etProductName.text
                val productDesc = etProductDescription.text
                val productPrice = etProductPrice.text
                val category = spnCategory.selectedItem.toString()
                val condition = spnCondition.selectedItem.toString()


                btnAddPhoto.setOnClickListener {
                    setBottomSheet()
                }

                btnAddProduct.setOnClickListener {
                    if (!productName.isNullOrEmpty() &&
                        !productDesc.isNullOrEmpty() &&
                        imageList.isNotEmpty() &&
                        !productPrice.isNullOrEmpty() &&
                        !etSize.text.isNullOrEmpty() &&
                        spnCategory.selectedItem.toString() != "Kategori seç"
                    ) {
                        loading.show(childFragmentManager, "loaderSell")

                        val bitmapList = arrayListOf<Bitmap>()
                        imageList.forEach {
                            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, it)
                            bitmapList.add(bitmap)
                        }

                        val gender = when {
                            male.isChecked -> {
                                male.text.toString()
                            }
                            female.isChecked -> {
                                female.text.toString()
                            }
                            else -> {
                                unisex.text.toString()
                            }
                        }

                        viewModel.addProductToMarket(
                            productName.toString(),
                            productPrice.toString(),
                            productDesc.toString(),
                            category,
                            etSize.text.toString(),
                            condition,
                            gender,
                            bitmapList,
                            viewLifecycleOwner
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                Constants.PRODUCT_ADDED -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(context)
                                        .setMessage(it)
                                        .setPositiveButton(Constants.OK) { _, _ ->
                                            navController.popBackStack()
                                        }.show()
                                }
                                else -> {
                                    loading.dismissAllowingStateLoss()
                                    AlertDialog.Builder(context)
                                        .setMessage(it)
                                        .setPositiveButton(Constants.OK) { _, _ -> }.show()
                                }
                            }
                        }
                    } else {
                        var warningString = ""
                        if (productName.isNullOrEmpty()) {
                            textInputLayoutProductName.error = Constants.FIELD_REQUIRED
                        }
                        if (productDesc.isNullOrEmpty()) {
                            textInputLayoutProductDescription.error = Constants.FIELD_REQUIRED
                        }
                        if (productPrice.isNullOrEmpty()) {
                            textInputLayoutProductPrice.error = Constants.FIELD_REQUIRED
                        }
                        if (imageList.isEmpty()) {
                            warningString += "- " + Constants.IMAGE_REQUIRED + "\n"
                        }
                        if (etSize.text.isNullOrEmpty() || spnCategory.selectedItem.toString() == "Kategori seç") {
                            warningString += "- " + Constants.PRODUCT_PROPERTIES_REQUIRED + "\n"
                        }
                        if (warningString.isNotEmpty()) {
                            showError(warningString)
                        }
                    }
                }

                expandable.parentLayout.setOnClickListener {
                    if (expandable.isExpanded) {
                        expandable.collapse()
                        spnCategory.setSelection(0)
                    } else {
                        expandable.expand()
                        unisex.isChecked = true
                        layoutSize.visibility = View.GONE
                        layoutCondition.visibility = View.GONE
                    }

                    val spnCategorySelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            layoutSize.visibility = View.VISIBLE
                            layoutCondition.visibility = View.VISIBLE

                            when (position) {
                                // Kategori sec
                                0 -> {
                                    layoutSize.visibility = View.GONE
                                    layoutCondition.visibility = View.GONE
                                }

                                // Giyim
                                1 -> {
                                    tvSize.text = "Giysi bedeni/boyu"
                                    etSize.inputType = InputType.TYPE_CLASS_TEXT
                                }

                                // Ayakkabi
                                2 -> {
                                    tvSize.text = "Ayakkabı numarası"
                                    etSize.inputType = InputType.TYPE_CLASS_NUMBER
                                }

                                else -> {
                                    tvSize.text = "Ürün boyutu"
                                    etSize.inputType = InputType.TYPE_CLASS_TEXT
                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    // Category spinner
                    spnCategory.onItemSelectedListener = spnCategorySelectedListener

                }
            }
        }
    }

    private fun showError(warningString: String) {
        AlertDialog.Builder(context)
            .setMessage(warningString)
            .setPositiveButton(Constants.OK) { _, _ -> }.show()
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentAddProductBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
            BaseViewModel::class.java
        )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::viewModel.isInitialized)
            viewModel.saveState()

        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageList.clear()

                    if (data.clipData != null) {
                        val count = data.clipData?.itemCount
                        for (i in 0 until count!!) {
                            Log.e("OpenMarket", "${data.clipData!!.getItemAt(i).uri}")
                            imageList.add(data.clipData!!.getItemAt(i).uri)
                        }
                        binding.sellPager.adapter = AddProductViewPagerAdapter(imageList)
                        Log.e("OpenMarket", "${binding.sellPager.childCount}")
                    } else if (data.data != null) {
                        Log.e("OpenMarket", "single item ${data.data}")
                        imageList.add(data.data!!)
                        binding.sellPager.adapter = AddProductViewPagerAdapter(imageList)
                    }
                }
            }
            CAMERA_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageList.clear()
                    val selectedImage = data.extras?.get("data") as Bitmap

                    val imageUri = parseAndGetImageUri(selectedImage)
                    imageList.add(imageUri)

                    Log.e("OpenMarket", imageList.toString())
                    binding.sellPager.adapter = AddProductViewPagerAdapter(imageList)
                }
            }
            else -> {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseAndGetImageUri(selectedImage: Bitmap): Uri {
        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.PNG, 75, baos)
        val path = MediaStore.Images.Media.insertImage(activity?.contentResolver, selectedImage, "Title", null)
        return Uri.parse(path)
    }

    private fun setBottomSheet() {
        val view = layoutInflater.inflate(R.layout.layout_bottom_dialog_sheet, null)
        val cameraSheet = view.findViewById<LinearLayout>(R.id.cameraSheet)
        val gallerySheet = view.findViewById<LinearLayout>(R.id.gallerySheet)

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)

        cameraSheet.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_PICK)
            dialog.dismiss()
        }

        gallerySheet.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, GALLERY_PICK)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setListenersForText(binding: FragmentAddProductBinding) {
        with(binding) {
            etProductName.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    textInputLayoutProductName.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    textInputLayoutProductName.isErrorEnabled = false
                }
            }
            etProductDescription.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    textInputLayoutProductDescription.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    textInputLayoutProductDescription.isErrorEnabled = false
                }
            }
            etProductPrice.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    textInputLayoutProductPrice.apply {
                        isErrorEnabled = true
                        error = Constants.FIELD_REQUIRED
                    }
                } else {
                    textInputLayoutProductPrice.isErrorEnabled = false
                }
            }
        }
    }
}