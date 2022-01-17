package com.alperen.openmarket.ui.main.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alperen.openmarket.R
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.utils.FirebaseInstance
import com.alperen.openmarket.utils.GlideApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Alperen on 14.01.2022.
 */
class HomepageViewPagerAdapter(private val list: ArrayList<Product>): RecyclerView.Adapter<HomepageViewPagerAdapter.HomepageViewPagerViewHolder>() {
    inner class HomepageViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val viewPagerImage: ImageView = itemView.findViewById(R.id.viewPagerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomepageViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_viewpager_product_item, parent, false)
        return HomepageViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomepageViewPagerViewHolder, position: Int) {
        val storageRef = FirebaseStorage.getInstance().reference.child(list[position].image[0])
        GlideApp.with(holder.itemView.context).load(storageRef).into(holder.viewPagerImage)

        holder.itemView.setOnClickListener {
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(FirebaseInstance.auth.currentUser?.uid!!)
                .child("user_recently_shown")
                .child(list[position].id)
                .setValue(list[position].id)

            val singleProduct = list[position]
            val action = HomepageFragmentDirections.actionHomepageFragmentToProductDetailFragment(singleProduct)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}