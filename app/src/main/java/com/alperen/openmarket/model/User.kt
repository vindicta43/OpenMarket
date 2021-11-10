package com.alperen.openmarket.model

/**
 * Created by Alperen on 6.11.2021.
 */
data class User(
    val username: String,
    val email: String,
    val name: String,
    val surname: String,
    val password: String,
    val added_product: Int = 0,
    val comment_count: Int = 0,
    val purchased_product: Int = 0
) {
    constructor() : this("", "", "", "", "", 0, 0, 0)
}