package com.fourcode.clients.fashion.product

import com.google.firebase.Timestamp

data class Product(
    val documentId: String,
    val brand: String,
    val description: String,
    val category: String,
    val image: String,
    val name: String,
    val price: Float,
    val dateCreated: Timestamp
)