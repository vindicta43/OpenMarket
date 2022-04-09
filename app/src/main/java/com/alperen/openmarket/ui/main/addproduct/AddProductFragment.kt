package com.alperen.openmarket.ui.main.addproduct

import android.app.Activity
import android.app.AlertDialog
import android.app.DirectAction
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
import com.alperen.openmarket.utils.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import java.io.ByteArrayOutputStream

class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)
        binding = FragmentAddProductBinding.inflate(inflater)

        val pageTitle = arrayOf("Satış", "Açık Artırma")
        with(binding) {
            pagerAddProduct.isUserInputEnabled = false
            pagerAddProduct.adapter = AddProductPagerAdapter(this@AddProductFragment)
            TabLayoutMediator(toolbar, pagerAddProduct) { tab, pos ->
                tab.text = pageTitle[pos]
            }.attach()
            
            return root
        }
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
}