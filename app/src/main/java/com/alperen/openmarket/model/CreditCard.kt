package com.alperen.openmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Alperen on 3.12.2021.
 */
@Parcelize
data class CreditCard(
    val id: String,
    val name: String,
    val number: String,
    val date: String,
    val cvv: String
) : Parcelable {
    constructor() : this("", "", "", "", "")
}