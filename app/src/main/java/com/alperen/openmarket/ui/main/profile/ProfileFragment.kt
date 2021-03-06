package com.alperen.openmarket.ui.main.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProfileBinding
import com.alperen.openmarket.model.User
import com.alperen.openmarket.model.UserSnapshot
import com.alperen.openmarket.ui.login.LoginActivity
import com.alperen.openmarket.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

const val GALLERY_PICK = 0
const val CAMERA_PICK = 1

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
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
            loading.show(childFragmentManager, "loaderProfile")
            rootLayout.visibility = View.INVISIBLE

            viewModel.getUserProfile(viewLifecycleOwner).observe(viewLifecycleOwner) {
                loading.dismissAllowingStateLoss()
                rootLayout.visibility = View.VISIBLE
                fillUserFields(it)

//                when (it) {
//                    Constants.SUCCESS -> {
//                        loading.dismissAllowingStateLoss()
//                        rootLayout.visibility = View.VISIBLE
//                        fillUserFields()
//                    }
//                    else -> {
//                        AlertDialog.Builder(context)
//                            .setMessage(it)
//                            .setPositiveButton(Constants.OK) { _, _ -> }
//                            .show()
//                    }
//                }
            }

            ivProfile.setOnClickListener {
                setBottomSheet()
            }

            cardAddedProducts.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_userAddedProductsFragment)
            }

            cardCreditCards.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_creditCardsFragment)
            }

            cardProfileSettings.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_accountSettingsFragment)
            }

            cardRecentlyShown.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_recentlyShownFragment)
            }

            cardFavorites.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_favoritesFragment)
            }

            cardPurchasedProducts.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_purchasedProductsFragment)
            }

            btnLogout.setOnClickListener {
                val sharedPref =
                    activity?.getSharedPreferences(Constants.APP_INIT, Context.MODE_PRIVATE)
                sharedPref?.edit()?.putBoolean(Constants.APP_INIT, true)?.apply()
                viewModel.logout()

                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            return root
        }
    }

    private fun fillUserFields(user: UserSnapshot) {
        with(binding) {
            if (user.profile_image == null) {
                ivProfile.setImageResource(R.drawable.ic_person)
            } else {
                val storageRef = FirebaseStorage.getInstance().reference.child(user.profile_image!!)
                GlideApp.with(requireContext()).load(storageRef).into(ivProfile)
            }
            tvUsername.text = user.username
            tvNameSurname.text = "${user.name} ${user.surname}"
            tvAddedProduct.text = user.added_product_count.toString()
            tvCommentCount.text = user.comment_count.toString()
            tvPurchasedProduct.text = user.purchased_product.toString()
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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_PICK)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loading.show(requireActivity().supportFragmentManager, "loaderProfile")
        when (requestCode) {
            GALLERY_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val image = data.data!!
                    binding.ivProfile.setImageURI(image)

                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, image)

                    viewModel.setProfilePicture(bitmap, viewLifecycleOwner).observe(viewLifecycleOwner) {
                        when (it) {
                            Constants.SUCCESS -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage(Constants.PROFILE_PHOTO_CHANGE)
                                    .setPositiveButton(Constants.OK) { _, _ -> }
                                    .show()
                            }
                            else -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage(it)
                                    .setPositiveButton(Constants.OK) { _, _ -> }
                                    .show()
                            }
                        }
                    }
                }
            }
            CAMERA_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras?.get("data") as Bitmap

                    val image = parseAndGetImageUri(selectedImage)
                    binding.ivProfile.setImageURI(image)

                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, image)

                    viewModel.setProfilePicture(bitmap, viewLifecycleOwner).observe(viewLifecycleOwner) {
                        when (it) {
                            Constants.SUCCESS -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage(Constants.PROFILE_PHOTO_CHANGE)
                                    .setPositiveButton(Constants.OK) { _, _ -> }
                                    .show()
                            }
                            else -> {
                                loading.dismissAllowingStateLoss()
                                AlertDialog.Builder(context)
                                    .setMessage(it)
                                    .setPositiveButton(Constants.OK) { _, _ -> }
                                    .show()
                            }
                        }
                    }
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

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentProfileBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
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
}