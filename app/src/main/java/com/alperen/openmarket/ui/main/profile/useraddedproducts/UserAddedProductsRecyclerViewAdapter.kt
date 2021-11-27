package com.alperen.openmarket.ui.main.profile.useraddedproducts

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
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Alperen on 21.11.2021.
 */
class UserAddedProductsRecyclerViewAdapter(private val list: ArrayList<Product>) :
    RecyclerView.Adapter<UserAddedProductsRecyclerViewAdapter.UserAddedProductsRecyclerViewHolder>() {
    inner class UserAddedProductsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserAddedProductImage = itemView.findViewById<ImageView>(R.id.ivUserAddedProductImage)
        val tvUserAddedProductName = itemView.findViewById<TextView>(R.id.tvUserAddedProductName)
        val tvUserAddedProductPrice = itemView.findViewById<TextView>(R.id.tvUserAddedProductPrice)
        val ibDelete = itemView.findViewById<ImageButton>(R.id.ibDelete)
        val ibEdit = itemView.findViewById<ImageButton>(R.id.ibEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAddedProductsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_added_products, parent, false)
        return UserAddedProductsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAddedProductsRecyclerViewHolder, position: Int) {
        with(holder) {
            val storageRef = FirebaseStorage.getInstance().reference.child(list[position].product_image[0])
            GlideApp.with(itemView.context).load(storageRef).into(ivUserAddedProductImage)
            tvUserAddedProductName.text = list[position].product_name
            tvUserAddedProductPrice.text = list[position].product_price.toString()

            ibDelete.setOnClickListener {

            }

            ibEdit.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}