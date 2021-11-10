package com.alperen.openmarket.ui.main.homepage

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.Product
import com.squareup.picasso.Picasso

/**
 * Created by Alperen on 28.10.2021.
 */
//const val RECENTLY_SHOWN = 0
//const val HOMEPAGE_ITEMS = 1
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
            Picasso.get().load(list[position].product_image).placeholder(R.drawable.ic_three_dot).into(ivProduct)
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