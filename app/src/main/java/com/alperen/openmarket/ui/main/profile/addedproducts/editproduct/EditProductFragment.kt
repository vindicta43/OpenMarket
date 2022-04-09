package com.alperen.openmarket.ui.main.profile.addedproducts.editproduct

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentEditProductBinding
import com.alperen.openmarket.utils.ProductViewPagerAdapter
import com.alperen.openmarket.ui.main.productdetail.ProductDetailViewPagerAdapter
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.utils.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.ByteArrayOutputStream

const val GALLERY_PICK = 0
const val CAMERA_PICK = 1

class EditProductFragment : Fragment() {
    private lateinit var binding: FragmentEditProductBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val loading by lazy { LoadingFragment() }
    private val args: EditProductFragmentArgs by navArgs()
    private val imageList = arrayListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            fillFields()
            return root
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            btnAddPhoto.setOnClickListener {
                setBottomSheet()
            }

            btnEditProduct.setOnClickListener {
                if (!etProductName.text.isNullOrEmpty() &&
                    !etProductDescription.text.isNullOrEmpty() &&

                    !etProductPrice.text.isNullOrEmpty() &&
                    !etSize.text.isNullOrEmpty() &&
                    spnCategory.selectedItem.toString() != "Kategori seç"
                ) {
                    loading.show(childFragmentManager, "loaderSell")

                    val update =  mutableMapOf<String, Any>()
                    if (imageList.isNotEmpty()) {
                        val bitmapList = arrayListOf<Bitmap>()
                        imageList.forEach {
                            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, it)
                            bitmapList.add(bitmap)
                        }
                        update["image"] = bitmapList
                    }

                    val gender = when {
                        btnMale.isChecked -> {
                            btnFemale.text.toString()
                        }
                        btnFemale.isChecked -> {
                            btnFemale.text.toString()
                        }
                        else -> {
                            btnUnisex.text.toString()
                        }
                    }

                    update["category"] = spnCategory.selectedItem.toString()
                    update["condition"] = spnCondition.selectedItem.toString()
                    update["description"] = etProductDescription.text.toString()
                    update["gender"] = gender
                    update["id"] = args.product.id
                    update["name"] = etProductName.text.toString()
                    update["price"] = etProductPrice.text.toString().toInt()
                    update["size"] = etSize.text.toString()
                    update["purchased"] = args.product.purchased
                    update["purchase_date"] = args.product.purchase_date
                    update["list_date"] = args.product.list_date

                    viewModel.updateProduct(update, viewLifecycleOwner).observe(viewLifecycleOwner) {
                        when(it) {
                            Constants.PRODUCT_UPDATED -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage(it)
                                    .setCancelable(false)
                                    .setPositiveButton(Constants.OK) { _, _ ->
                                        navController.popBackStack()
                                    }.show()
                            }
                            else -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage(it)
                                    .setCancelable(false)
                                    .setPositiveButton(Constants.OK) { _, _ -> }
                                    .show()
                            }
                        }
                    }
                } else {
                    var warningString = ""
                    if (etProductName.text.isNullOrEmpty()) {
                        textInputLayoutProductName.error = Constants.FIELD_REQUIRED
                    }
                    if (etProductDescription.text.isNullOrEmpty()) {
                        textInputLayoutProductDescription.error = Constants.FIELD_REQUIRED
                    }
                    if (etProductPrice.text.isNullOrEmpty()) {
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
        }
    }

    private fun showError(warningString: String) {
        AlertDialog.Builder(context)
            .setMessage(warningString)
            .setPositiveButton(Constants.OK) { _, _ -> }.show()
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
                        binding.sellPager.adapter = ProductViewPagerAdapter(imageList)
                        Log.e("OpenMarket", "${binding.sellPager.childCount}")
                    } else if (data.data != null) {
                        Log.e("OpenMarket", "single item ${data.data}")
                        imageList.add(data.data!!)
                        binding.sellPager.adapter = ProductViewPagerAdapter(imageList)
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
                    binding.sellPager.adapter = ProductViewPagerAdapter(imageList)
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

    private fun fillFields() {
        with(binding) {
            // Viewpager
            sellPager.adapter = ProductDetailViewPagerAdapter(args.product.image)

            // Radiobutton check
            checkRadioButton()

            // Category spinner select
            selectSpinners()

            etProductName.setText(args.product.name)
            etProductDescription.setText(args.product.description)
            etProductPrice.setText(args.product.price.toString())


            // Imagelist

            etSize.setText(args.product.size)
        }
    }

    private fun selectSpinners() {
        val categoryArray = resources.getStringArray(R.array.add_product_category)
        val conditionArray = resources.getStringArray(R.array.add_product_condition)

        with(binding) {
            // Category
            when (args.product.category) {
                categoryArray[1] -> {
                    spnCategory.setSelection(1)
                }
                categoryArray[2] -> {
                    spnCategory.setSelection(2)
                }
                categoryArray[3] -> {
                    spnCategory.setSelection(3)
                }
                categoryArray[4] -> {
                    spnCategory.setSelection(4)
                }
                categoryArray[5] -> {
                    spnCategory.setSelection(5)
                }
                categoryArray[6] -> {
                    spnCategory.setSelection(6)
                }
                categoryArray[7] -> {
                    spnCategory.setSelection(7)
                }
                categoryArray[8] -> {
                    spnCategory.setSelection(8)
                }
                categoryArray[9] -> {
                    spnCategory.setSelection(9)
                }
                categoryArray[10] -> {
                    spnCategory.setSelection(10)
                }
                categoryArray[11] -> {
                    spnCategory.setSelection(11)
                }
            }

            // Condition
            when (args.product.condition) {
                conditionArray[0] -> {
                    spnCondition.setSelection(0)
                }
                conditionArray[1] -> {
                    spnCondition.setSelection(1)
                }
                conditionArray[2] -> {
                    spnCondition.setSelection(2)
                }
                conditionArray[3] -> {
                    spnCondition.setSelection(3)
                }
                conditionArray[4] -> {
                    spnCondition.setSelection(4)
                }
            }
        }
    }

    private fun checkRadioButton() {
        with(binding) {
            when (args.product.gender) {
                "Kadın" -> {
                    btnFemale.isChecked = true
                }
                "Erkek" -> {
                    btnFemale.isChecked = true
                }
                else -> {
                    btnUnisex.isChecked = true
                }
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentEditProductBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}