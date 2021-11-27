package com.alperen.openmarket.ui.main.profile.useraddedproducts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentUserAddedProductsBinding
import com.alperen.openmarket.utils.LoadingFragment
import com.alperen.openmarket.viewmodel.BaseViewModel

class UserAddedProductsFragment : Fragment() {
    private lateinit var binding: FragmentUserAddedProductsBinding
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
            btnBack.setOnClickListener {
                navController.popBackStack()
            }

            loading.show(requireActivity().supportFragmentManager, "loaderAddedProducts")
            viewModel.getUserProducts(viewLifecycleOwner).observe(viewLifecycleOwner) {
                loading.dismissAllowingStateLoss()
                recyclerUserAddedProducts.adapter = UserAddedProductsRecyclerViewAdapter(it)
                recyclerUserAddedProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            return root
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentUserAddedProductsBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}