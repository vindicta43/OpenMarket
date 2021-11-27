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
            productDetailPager.adapter = ProductDetailViewPagerAdapter(args.product.product_image)
            tvProductDetailDescription.text = "Ürün adı: ${args.product.product_name}\n" +
                    "Ürün açıklaması: ${args.product.product_description}\n" +
                    "Ürün fiyatı: ${args.product.product_price}"

            return root
        }
    }
}