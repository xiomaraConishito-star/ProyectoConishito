package com.cibertec.conishitoapp.ui.store

import java.util.UUID

@Suppress("unused")
data class Purchase(
    val id: String = UUID.randomUUID().toString(),
    val items: List<CartItem>,
    val total: Double,
    val buyerName: String,
    val address: String,
    val paymentMethod: String,
    val date: Long = System.currentTimeMillis()
)
