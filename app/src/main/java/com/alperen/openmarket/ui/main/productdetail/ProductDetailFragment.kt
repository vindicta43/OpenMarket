package com.alperen.openmarket.ui.main.productdetail

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProductDetailBinding
import com.alperen.openmarket.utils.BaseViewModel
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.LoadingFragment

class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewModel: BaseViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val loading by lazy { LoadingFragment() }
    private val args: ProductDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initLateinitVariables(inflater)

        with(binding) {
            productDetailPager.adapter = ProductDetailViewPagerAdapter(args.product.image)

            tvProductName.text = args.product.name
            tvProductCondition.text = args.product.condition
            tvProductGender.text = args.product.gender
            tvProductSize.text = args.product.size
            tvProductCategory.text = args.product.category
            tvProductDescription.text = args.product.description
            tvProductPrice.text = args.product.price.toString()

            if (args.product.purchased) {
                btnPurchase.isEnabled = false
                tvProductIsPurchased.text = "Bu ürün satıldı"
            }

            return root
        }
    }

    override fun onResume() {
        super.onResume()

        with(binding) {
            btnPurchase.setOnClickListener {
                loading.show(childFragmentManager, "loaderPurchase")
                viewModel.purchaseProduct(args.product, viewLifecycleOwner).observe(viewLifecycleOwner) {
                    when (it) {
                        Constants.PRODUCT_PURCHASED -> {
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
                                .setPositiveButton(Constants.OK) { _, _ -> }.show()
                        }
                    }
                }
            }
        }
    }

    private fun initLateinitVariables(inflater: LayoutInflater) {
        binding = FragmentProductDetailBinding.inflate(inflater)
        viewModel =
            ViewModelProvider(this, SavedStateViewModelFactory(activity?.application, this)).get(
                BaseViewModel::class.java
            )
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
    }
}