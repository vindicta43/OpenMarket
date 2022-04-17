package com.alperen.openmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Created by Alperen on 10.11.2021.
 */
@Parcelize
data class Product(
    var id: String,
    var seller_id: String,
    var name: String,
    var price: Int,
    var description: String,
    var category: String,
    var size: String,
    var condition: String,
    var gender: String,
    var purchased: Boolean,
    var list_date: String,
    var purchase_date: String?,
    var image: List<String>,
    var productType: PRODUCT_TYPE?,
    var expiration_date: String?,
    var starting_price: Int?,
    var increment_multiplier: Int?,
    var last_offers: MutableList<OfferModel>?,
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        0,
        "",
        "",
        "",
        "",
        "",
        false,
        "",
        "",
        listOf(),
        null,
        "",
        0,
        null,
        mutableListOf()
    )
}

@Parcelize
data class OfferModel(
    var offerer_id: String,
    var offer_date: String,
    var increment: Int,
    var name_surname: String,
) : Parcelable {
    constructor(): this(
        "",
        "",
        0,
        ""
    )
}

enum class PRODUCT_TYPE {
    SELL,
    AUCTION
}