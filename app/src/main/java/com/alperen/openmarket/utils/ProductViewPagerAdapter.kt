package com.alperen.openmarket.utils

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R

/**
 * Created by Alperen on 19.11.2021.
 */
class ProductViewPagerAdapter(private val list: ArrayList<Uri>): RecyclerView.Adapter<ProductViewPagerAdapter.AddProductViewPagerViewHolder>() {
    inner class AddProductViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val viewPagerImage = itemView.findViewById<ImageView>(R.id.viewPagerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_viewpager_product_item, parent, false)
        return AddProductViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddProductViewPagerViewHolder, position: Int) {
        GlideApp.with(holder.itemView.context).load(list[position]).into(holder.viewPagerImage)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}