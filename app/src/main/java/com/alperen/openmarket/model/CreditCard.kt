package com.alperen.openmarket.model

/**
 * Created by Alperen on 3.12.2021.
 */
data class CreditCard(
    val id: String,
    val name: String,
    val number: String,
    val date: String,
    val cvv: String
) {
    constructor() : this("", "", "", "", "")
}