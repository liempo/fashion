package com.fourcode.clients.fashion.product

import com.google.firebase.Timestamp

data class Product(
    val documentId: String,
    val brand: String,
    val category: String,
    val dateCreated: Timestamp,
    val description: String,
    val image: String,
    val material: String,
    val name: String,
    val price: Float,
    val stock: Float,
    val userId: String
)