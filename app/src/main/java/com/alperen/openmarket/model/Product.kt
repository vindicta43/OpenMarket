package com.alperen.openmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Alperen on 10.11.2021.
 */
@Parcelize
data class Product(
    val id: String,
    val seller_id: String,
    val name: String,
    val price: Int,
    val description: String,
    val category: String,
    val size: String,
    val condition: String,
    val gender: String,
    var purchased: Boolean,
    val list_date: String,
    var purchase_date: String,
    val image: List<String>,
) : Parcelable {
    constructor() : this("", "", "", 0, "", "", "", "", "", false, "", "", listOf())
}