package com.alperen.openmarket.ui.main.addproduct.pages.sell

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentSellBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.jar.Manifest

const val GALLERY_PICK = 1
const val CAMERA_PICK = 2

class SellFragment : Fragment() {
    private lateinit var binding: FragmentSellBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val loading by lazy { LoadingFragment() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            return root
        }
    }

    override fun onResume() {
        val productName = binding.etProductName.text
        val productDescription = binding.etProductDescription.text
        val productImage = binding.ivAddProductImage
        val productPrice = binding.etProductPrice.text

        super.onResume()

        setListenersForText(binding)
        with(binding) {
            ivAddProductImage.setOnClickListener {
                setBottomSheet()
            }

            btnAddProduct.setOnClickListener {
                if (!productName.isNullOrEmpty() && !productDescription.isNullOrEmpty() && productImage.drawable != null && !productPrice.isNullOrEmpty()) {
                    loading.show(childFragmentManager, "loaderSell")
                    viewModel.addProductToMarket(
                        productName.toString(),
                        productDescription.toString(),
                        productPrice.toString(),
                        productImage,
                        viewLifecycleOwner
                    )
                        .observe(viewLifecycleOwner) {
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
                                        .setPositiveButton(Constants.OK) { _, _ ->

                                        }.show()
                                }
                            }
                        }
                } else {
                    if (productName.isNullOrEmpty()) {
                        textInputLayoutProductName.error = Constants.FIELD_REQUIRED

                    }
                    if (productDescription.isNullOrEmpty()) {
                        textInputLayoutProductDescription.error = Constants.FIELD_REQUIRED
                    }
                    if (productPrice.isNullOrEmpty()) {
                        textInputLayoutProductPrice.error = Constants.FIELD_REQUIRED
                    }
                    if (productImage.drawable == null) {
                        AlertDialog.Builder(context)
                            .setMessage(Constants.IMAGE_REQUIRED)
                            .setPositiveButton(Constants.OK) { _, _ ->

                            }.show()
                    }
                }
            }
        }
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
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_PICK)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setListenersForText(binding: FragmentSellBinding) {
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

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentSellBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
            BaseViewModel::class.java
        )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    binding.ivAddProductImage.scaleType = ImageView.ScaleType.FIT_XY
                    binding.ivAddProductImage.setImageURI(selectedImage)
                }
            }
            CAMERA_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras?.get("data") as Bitmap
                    Toast.makeText(context, selectedImage.toString(), Toast.LENGTH_SHORT).show()
                    binding.ivAddProductImage.scaleType = ImageView.ScaleType.FIT_XY
                    binding.ivAddProductImage.setImageBitmap(selectedImage)
                }
            } else ->{
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::viewModel.isInitialized)
            viewModel.saveState()

        super.onSaveInstanceState(outState)
    }
}