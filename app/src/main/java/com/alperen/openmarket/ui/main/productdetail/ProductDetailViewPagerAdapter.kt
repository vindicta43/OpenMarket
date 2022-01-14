package com.alperen.openmarket.ui.main.productdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.alperen.openmarket.R
import com.alperen.openmarket.utils.GlideApp
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Alperen on 20.11.2021.
 */
class ProductDetailViewPagerAdapter(private val list: List<String>): RecyclerView.Adapter<ProductDetailViewPagerAdapter.ProductDetailViewPagerViewHolder>() {
    inner class ProductDetailViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val viewPagerImage: ImageView = itemView.findViewById(R.id.viewPagerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_viewpager_product_item, parent, false)
        return ProductDetailViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductDetailViewPagerViewHolder, position: Int) {
        val storageRef = FirebaseStorage.getInstance().reference.child(list[position])
        GlideApp.with(holder.itemView.context).load(storageRef).into(holder.viewPagerImage)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}