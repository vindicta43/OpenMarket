package com.alperen.openmarket.ui.main.addproduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.FragmentAddProductBinding
import com.alperen.openmarket.ui.main.addproduct.pages.auction.AuctionFragment
import com.alperen.openmarket.ui.main.addproduct.pages.sell.SellFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AddProductFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddProductBinding.inflate(inflater)

        with(binding) {
            val titleList = listOf("Satış", "Açık Artırma")

            val adapter = AddProductAdapter(requireActivity().supportFragmentManager, lifecycle)
            viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = titleList[position]
            }.attach()

            return root
        }
    }
}