package com.alperen.openmarket.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alperen.openmarket.R
import com.alperen.openmarket.model.Product
import com.google.firebase.database.FirebaseDatabase


/**
 * Created by Alperen on 30.05.2022.
 */

private const val CHANNEL_ID = "AuctionChannel"
private const val CHANNEL_NAME = "Auction"
private const val CHANNEL_DESCRIPTION = "MakeAuction"
private const val NOTIFICATION_ID = 1

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
                val product = it.getValue(Product::class.java)!!
                Log.d("auctionService", "firebase product ${product.toString()}")
                Log.d("auctionService", "locale product id: $productId expDate: $expDate price: $incrementValue")
                if (FirebaseInstance.auth.currentUser?.uid == product.last_offers?.last()?.offerer_id) {
                    FirebaseInstance.purchaseProduct(product).observeForever { resultString ->
                        when (resultString) {
                            Constants.PRODUCT_PURCHASED -> {
                                // Send notification if user get the product
                                Log.d("auctionService", "Product purchased")
                                Log.d("auctionService", "id: $productId expDate: $expDate price: $incrementValue")

                                val notification = NotificationCompat.Builder(context!!, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_onboarding_2)
                                    .setContentTitle("Ürün Satın Alındı")
                                    .setContentText("${product.name} ürününü açık artırmada ${product.price} fiyata satın aldınız.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                                val notificationManager =
                                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                                createNotificationChannel(notificationManager)

                                notificationManager.notify(1, notification.build())
                            }
                        }
                    }
                }
            }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
        }
        notificationManager.createNotificationChannel(channel)
    }
}