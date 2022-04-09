package com.alperen.openmarket.ui.main.addproduct

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alperen.openmarket.ui.main.addproduct.auction.AuctionFragment
import com.alperen.openmarket.ui.main.addproduct.sell.SellFragment

/**
 * Created by Alperen on 3.04.2022.
 */

const val PAGE_NUM = 2

class AddProductPagerAdapter(val fa: Fragment): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = PAGE_NUM

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SellFragment()
        } else {
            AuctionFragment()
        }
    }


}