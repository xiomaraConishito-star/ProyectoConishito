package com.cibertec.conishitoapp.ui.store

@Suppress("unused")
data class CartItem(
    val product: StoreItem,
    var quantity: Int = 1
) {
    fun subtotal(): Double {
        // price stored as string like "S/ 25.00". Convert to double
        val cleaned = product.price.replace("S/", "").trim().replace(',', '.')
        return try {
            cleaned.toDouble() * quantity
        } catch (_: Exception) {
            0.0
        }
    }
}
