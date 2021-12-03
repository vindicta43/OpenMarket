package com.alperen.openmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Alperen on 6.11.2021.
 */
@Parcelize
data class User(
    val id: String,
    val username: String,
    val email: String,
    val name: String,
    val surname: String,
    val added_product_count: Int = 0,
    val comment_count: Int = 0,
    val purchased_product: Int = 0,
    val added_products: List<Product> = listOf()
) : Parcelable {
    constructor() : this("", "", "", "", "", 0, 0, 0, listOf())
}