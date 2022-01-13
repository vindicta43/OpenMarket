package com.alperen.openmarket.ui.main.productdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProductDetailBinding
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.ui.main.addproduct.AddProductViewPagerAdapter

class ProductDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProductDetailBinding.inflate(inflater)
        val args: ProductDetailFragmentArgs by navArgs()

        with(binding) {
            productDetailPager.adapter = ProductDetailViewPagerAdapter(args.product.image)

            tvProductName.text = args.product.name
            tvProductCondition.text = args.product.condition
            tvProductGender.text = args.product.gender
            tvProductSize.text = args.product.size
            tvProductCategory.text = args.product.category
            tvProductDescription.text = args.product.description
            tvProductPrice.text = args.product.price.toString()

            return root
        }
    }
}