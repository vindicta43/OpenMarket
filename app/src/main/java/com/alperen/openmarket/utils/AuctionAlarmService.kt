package com.alperen.openmarket.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.alperen.openmarket.model.Product
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Alperen on 30.05.2022.
 */
class AuctionAlarmService : BroadcastReceiver() {

    companion object {
        var productId: String = ""
        var expDate: String = ""
        var incrementValue: Int = 0
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        productId = intent?.extras?.getString("productId").toString()
        expDate = intent?.extras?.getString("expDate").toString()
        incrementValue = intent?.extras?.getInt("incrementValue", 0) ?: 0

        Log.d("auctionService", "intent receive $productId $expDate $incrementValue")

        FirebaseDatabase.getInstance().reference
            .child("products")
            .child(productId)
            .get()
            .addOnSuccessListener {
                val product = it.getValue(Product::class.java)
                Log.d("auctionService", "firebase product ${product.toString()}")
                Log.d("auctionService", "locale product id: $productId expDate: $expDate price: $incrementValue")
                if (incrementValue == product?.price) {
                    FirebaseInstance.purchaseProduct(product).observeForever { resultString ->
                        when(resultString) {
                            Constants.PRODUCT_PURCHASED -> {
                                // Stop service
                                Log.d("auctionService", "Product purchased")
                                Log.d("auctionService", "id: $productId expDate: $expDate price: $incrementValue")
                                Toast.makeText(context, "${product.name} ürününü açık artırmada ${product.price} fiyata satın aldınız.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
    }
}