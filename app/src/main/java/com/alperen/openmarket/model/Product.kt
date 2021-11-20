package com.alperen.openmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.ByteArrayOutputStream

/**
 * Created by Alperen on 10.11.2021.
 */
@Parcelize
data class Product(
    val id: String,
    val product_name: String,
    val product_price: Int,
    val product_description: String,
    val product_image: List<String>,
) : Parcelable {
    constructor() : this("", "", 0, "", listOf())
}