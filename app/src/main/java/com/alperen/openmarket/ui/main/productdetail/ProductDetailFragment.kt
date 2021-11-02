package com.alperen.openmarket.ui.main.productdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProductDetailBinding.inflate(inflater)

        with(binding) {

            return root
        }
    }
}