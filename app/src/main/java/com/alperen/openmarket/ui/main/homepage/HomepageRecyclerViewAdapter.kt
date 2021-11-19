package com.alperen.openmarket.ui.main.homepage

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.GlideApp
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Alperen on 28.10.2021.
 */
class HomepageRecyclerViewAdapter(private val list: ArrayList<Product>) :
    RecyclerView.Adapter<HomepageRecyclerViewAdapter.HomePageRecyclerViewHolder>() {

    inner class HomePageRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct = itemView.findViewById<ImageView>(R.id.ivProduct)
        val btnFavorite = itemView.findViewById<ImageButton>(R.id.btnFavorite)
        val tvProductName = itemView.findViewById<TextView>(R.id.tvProductName)
        val tvProductPrice = itemView.findViewById<TextView>(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_homepage_product, parent, false)
        return HomePageRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePageRecyclerViewHolder, position: Int) {
        with(holder) {
            val storageRef = FirebaseStorage.getInstance().reference.child(list[position].product_image[0])
            GlideApp.with(holder.itemView.context).load(storageRef).into(ivProduct)
            tvProductName.text = list[position].product_name
            tvProductPrice.text = list[position].product_price.toString()

            btnFavorite.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}