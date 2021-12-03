package com.alperen.openmarket.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Alperen on 21.11.2021.
 */
@Parcelize
data class UserSnapshot(
    val id: String,
    val username: String,
    val email: String,
    val name: String,
    val surname: String,
    val added_product_count: Int = 0,
    val comment_count: Int = 0,
    val purchased_product: Int = 0,
    var profile_image: Uri? = null
) : Parcelable {
    constructor() : this("", "", "", "", "", 0, 0, 0, null)
}