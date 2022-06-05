package com.alperen.openmarket.utils

import android.os.Handler
import android.os.Looper
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
import com.alperen.openmarket.model.PRODUCT_TYPE
import com.alperen.openmarket.model.Product
import com.alperen.openmarket.ui.main.homepage.HomepageFragmentDirections
import com.alperen.openmarket.ui.main.profile.favorites.FavoritesFragmentDirections
import com.alperen.openmarket.ui.main.profile.purchasedproducts.PurchasedProductsFragmentDirections
import com.alperen.openmarket.ui.main.profile.recentlyshown.RecentlyShownFragmentDirections
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.TimeUnit

/**
 * Created by Alperen on 28.10.2021.
 */

const val sell = 0
const val auction = 1

class ProductRecyclerViewAdapter(private val list: ArrayList<Product>, private val className: String?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var handler: Handler

    inner class AuctionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivProductAuction: ImageView = itemView.findViewById(R.id.ivProductAuction)
        val btnFavoriteAuction: ImageButton = itemView.findViewById(R.id.btnFavoriteAuction)
        val tvProductNameAuction: TextView = itemView.findViewById(R.id.tvProductNameAuction)
        val tvProductPriceAuction: TextView = itemView.findViewById(R.id.tvProductPriceAuction)
        val tvAuctionCountdown: TextView = itemView.findViewById(R.id.tvAuctionCountdown)
    }

    inner class SellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductSell: ImageView = itemView.findViewById(R.id.ivProductSell)
        val btnFavoriteSell: ImageButton = itemView.findViewById(R.id.btnFavoriteSell)
        val tvProductNameSell: TextView = itemView.findViewById(R.id.tvProductNameSell)
        val tvProductPriceSell: TextView = itemView.findViewById(R.id.tvProductPriceSell)
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].productType == PRODUCT_TYPE.SELL)
            sell
        else
            auction
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            sell -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_homepage_sell_item, parent, false)
                SellViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_homepage_auction_item, parent, false)
                AuctionViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            sell -> {
                val holderSell = holder as SellViewHolder
                with(holderSell) {
                    val storageRef = FirebaseStorage.getInstance().reference.child(list[position].image[0])
                    GlideApp.with(holder.itemView.context).load(storageRef).into(ivProductSell)
                    tvProductNameSell.text = list[position].name
                    tvProductPriceSell.text = list[position].price.toString()

                    // TODO: halledildi ama tekrardan düzenle
                    checkFavoriteButton(list[position], btnFavoriteSell)

                    // TODO: halledildi ama tekrardan düzenle
                    btnFavoriteSell.setOnClickListener {
                        FirebaseInstance.getUserFavorites().observeForever { favoriteList ->
                            if (favoriteList?.contains(list[position]) == false) {
                                FirebaseDatabase.getInstance().reference
                                    .child("users")
                                    .child(FirebaseInstance.auth.currentUser?.uid!!)
                                    .child("user_favorites")
                                    .child(list[position].id)
                                    .setValue(list[position].id)

                                btnFavoriteSell.setImageResource(R.drawable.ic_favorite)
                            } else {
                                FirebaseDatabase.getInstance().reference
                                    .child("users")
                                    .child(FirebaseInstance.auth.currentUser?.uid!!)
                                    .child("user_favorites")
                                    .child(list[position].id)
                                    .removeValue()

                                btnFavoriteSell.setImageResource(R.drawable.ic_favorite_outline)
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
            else -> {
                val holderAuction = holder as AuctionViewHolder
                handler = Handler(Looper.getMainLooper())
                with(holderAuction) {
                    fun getDate(milliSeconds: Long): String {
                        val days = TimeUnit.MILLISECONDS.toDays(milliSeconds)
                        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds) % 24
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % 60
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % 60

                        return "${days}g ${hours}s ${minutes}d $seconds saniye"
                    }

                    fun updateText(expDate: String?) {
                        val time = System.currentTimeMillis()

                        val remainingTime = (expDate?.toLong() ?: 0) - time
                        var remainingString = ""
                        if (remainingTime <= 0) {
                            tvAuctionCountdown.text = "Açık artırmanın süresi doldu"
                        } else {
                            remainingString = getDate(remainingTime)
                            tvAuctionCountdown.text = remainingString
                        }
                    }

                    val updateTask = object : Runnable {
                        override fun run() {
                            if (holderAuction.layoutPosition != RecyclerView.NO_POSITION) {
                                updateText(list[holderAuction.layoutPosition].expiration_date)
                                handler.postDelayed(this, 1000)
                            }
                        }
                    }
                    handler.post(updateTask)

                    val storageRef = FirebaseStorage.getInstance().reference.child(list[position].image[0])
                    GlideApp.with(holder.itemView.context).load(storageRef).into(ivProductAuction)
                    tvProductNameAuction.text = list[position].name
                    tvProductPriceAuction.text = list[position].price.toString()

                    // TODO: halledildi ama tekrardan düzenle
                    checkFavoriteButton(list[position], btnFavoriteAuction)

                    // TODO: halledildi ama tekrardan düzenle
                    btnFavoriteAuction.setOnClickListener {
                        FirebaseInstance.getUserFavorites().observeForever { favoriteList ->
                            if (favoriteList?.contains(list[position]) == false) {
                                FirebaseDatabase.getInstance().reference
                                    .child("users")
                                    .child(FirebaseInstance.auth.currentUser?.uid!!)
                                    .child("user_favorites")
                                    .child(list[position].id)
                                    .setValue(list[position].id)

                                btnFavoriteAuction.setImageResource(R.drawable.ic_favorite)
                            } else {
                                FirebaseDatabase.getInstance().reference
                                    .child("users")
                                    .child(FirebaseInstance.auth.currentUser?.uid!!)
                                    .child("user_favorites")
                                    .child(list[position].id)
                                    .removeValue()

                                btnFavoriteAuction.setImageResource(R.drawable.ic_favorite_outline)
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
        }
    }
}