package com.alperen.openmarket.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.alperen.openmarket.utils.FirebaseInstance
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Alperen on 28.10.2021.
 */
class SearchRecyclerViewAdapter(val list: ArrayList<Product>, val communication: FragmentCommunication) : RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSearchItemResult = itemView.findViewById<TextView>(R.id.tvSearchResult)
        val tvSearchCategory = itemView.findViewById<TextView>(R.id.tvSearchCategory)
        val ibSearchText = itemView.findViewById<ImageView>(R.id.ibSearchText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            tvSearchItemResult.text = list[position].name
            tvSearchCategory.text = list[position].category

            ibSearchText.setOnClickListener {
                communication.respond(list[position])
            }

            itemView.setOnClickListener {
                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(FirebaseInstance.auth.currentUser?.uid!!)
                    .child("user_recently_shown")
                    .child(list[position].id)
                    .setValue(list[position].id)

                val singleProduct = list[position]
                val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(singleProduct)
                itemView.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface FragmentCommunication {
    fun respond(product: Product)
}