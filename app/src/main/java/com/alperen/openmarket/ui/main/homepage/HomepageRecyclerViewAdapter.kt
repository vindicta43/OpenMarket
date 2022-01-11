package com.alperen.openmarket.ui.main.homepage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.GlideApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Alperen on 28.10.2021.
 */
class HomepageRecyclerViewAdapter(private val list: ArrayList<Product>) :
    RecyclerView.Adapter<HomepageRecyclerViewAdapter.HomePageRecyclerViewHolder>() {

    inner class HomePageRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_homepage_product_item, parent, false)
        return HomePageRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePageRecyclerViewHolder, position: Int) {
        with(holder) {
            val storageRef = FirebaseStorage.getInstance().reference.child(list[position].image[0])
            GlideApp.with(holder.itemView.context).load(storageRef).into(ivProduct)
            tvProductName.text = list[position].name
            tvProductPrice.text = list[position].price.toString()

            btnFavorite.setOnClickListener {

            }

            itemView.setOnClickListener {
                Log.e("OpenMarket", "${list[position].id} ${list[position].name}")

                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(FirebaseInstance.auth.currentUser?.uid!!)
                    .child("user_recently_shown")
                    .child(list[position].id)
                    .setValue(list[position])

                val singleProduct = list[position]
                val action = HomepageFragmentDirections.actionHomepageFragmentToProductDetailFragment(singleProduct)
                itemView.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}