package com.alperen.openmarket.model

/**
 * Created by Alperen on 10.11.2021.
 */
data class Product(
    val product_name: String,
    val product_price: Int,
    val product_image: String,
) {
    constructor() : this("", 0, "")
}