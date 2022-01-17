package com.alperen.openmarket.utils

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
import com.alperen.openmarket.ui.main.homepage.HomepageFragmentDirections
import com.alperen.openmarket.ui.main.profile.favorites.FavoritesFragmentDirections
import com.alperen.openmarket.ui.main.profile.purchasedproducts.PurchasedProductsFragmentDirections
import com.alperen.openmarket.ui.main.profile.recentlyshown.RecentlyShownFragmentDirections
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Alperen on 28.10.2021.
 */
class ProductRecyclerViewAdapter(private val list: ArrayList<Product>, private val className: String?) :
    RecyclerView.Adapter<ProductRecyclerViewAdapter.HomePageRecyclerViewHolder>() {

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

            // TODO: halledildi ama tekrardan düzenle
            checkFavoriteButton(list[position], btnFavorite)

            // TODO: halledildi ama tekrardan düzenle
            btnFavorite.setOnClickListener {
                FirebaseInstance.getUserFavorites().observeForever { favoriteList ->
                    if (favoriteList?.contains(list[position]) == false) {
                        FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(FirebaseInstance.auth.currentUser?.uid!!)
                            .child("user_favorites")
                            .child(list[position].id)
                            .setValue(list[position].id)

                        btnFavorite.setImageResource(R.drawable.ic_favorite)
                    } else {
                        FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(FirebaseInstance.auth.currentUser?.uid!!)
                            .child("user_favorites")
                            .child(list[position].id)
                            .removeValue()

                        btnFavorite.setImageResource(R.drawable.ic_favorite_outline)
                    }
                }
            }

            itemView.setOnClickListener {
                Log.e("OpenMarket", "${list[position].id} ${list[position].name}")

                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(FirebaseInstance.auth.currentUser?.uid!!)
                    .child("user_recently_shown")
                    .child(list[position].id)
                    .setValue(list[position].id)

                when (className) {
                    "FavoritesFragment" -> {
                        val singleProduct = list[position]
                        val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(singleProduct)
                        itemView.findNavController().navigate(action)
                    }
                    "RecentlyShownFragment" -> {
                        val singleProduct = list[position]
                        val action = RecentlyShownFragmentDirections.actionRecentlyShownFragmentToProductDetailFragment(singleProduct)
                        itemView.findNavController().navigate(action)
                    }
                    "HomepageFragment" -> {
                        val singleProduct = list[position]
                        val action = HomepageFragmentDirections.actionHomepageFragmentToProductDetailFragment(singleProduct)
                        itemView.findNavController().navigate(action)
                    }
                    "PurchasedProductsFragment" -> {
                        val singleProduct = list[position]
                        val action = PurchasedProductsFragmentDirections.actionPurchasedProductsFragmentToProductDetailFragment(singleProduct)
                        itemView.findNavController().navigate(action)
                    }
                }
            }
        }
    }

    // TODO: halledildi ama tekrardan düzenle
    private fun checkFavoriteButton(product: Product, btnFavorite: ImageButton) {
        FirebaseInstance.getUserFavorites().observeForever { favoriteList ->
            if (favoriteList?.contains(product) == true) {
                btnFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_outline)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}