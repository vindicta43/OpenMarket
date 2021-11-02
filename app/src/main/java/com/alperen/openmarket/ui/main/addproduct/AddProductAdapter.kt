package com.alperen.openmarket.ui.main.addproduct

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alperen.openmarket.ui.main.addproduct.pages.auction.AuctionFragment
import com.alperen.openmarket.ui.main.addproduct.pages.sell.SellFragment

/**
 * Created by Alperen on 2.11.2021.
 */

const val PAGE_NUM = 2

class AddProductAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return PAGE_NUM
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) SellFragment() else AuctionFragment()
    }

}