package com.cibertec.conishitoapp.ui.store

@Suppress("unused")
object CartManager {
    private val items = mutableListOf<CartItem>()

    fun addProduct(product: StoreItem, qty: Int = 1) {
        val existing = items.find { it.product.name == product.name }
        if (existing != null) {
            existing.quantity += qty
        } else {
            items.add(CartItem(product, qty))
        }
    }

    fun removeProduct(product: StoreItem) {
        items.removeAll { it.product.name == product.name }
    }

    fun updateQuantity(product: StoreItem, qty: Int) {
        val existing = items.find { it.product.name == product.name }
        existing?.let {
            if (qty <= 0) removeProduct(product) else it.quantity = qty
        }
    }

    fun clear() {
        items.clear()
    }

    fun allItems(): List<CartItem> = items.toList()

    fun total(): Double = items.sumOf { it.subtotal() }

    fun isEmpty(): Boolean = items.isEmpty()
}
