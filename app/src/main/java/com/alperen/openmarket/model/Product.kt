package com.alperen.openmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Alperen on 10.11.2021.
 */
@Parcelize
data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val description: String,
    val category: String,
    val size: String,
    val condition: String,
    val gender: String,
    val image: List<String>,
) : Parcelable {
    constructor() : this("", "", 0, "", "", "", "", "", listOf())
}