package com.cibertec.conishitoapp.ui.store

@Suppress("unused")
object PurchaseManager {
    private val purchases = mutableListOf<Purchase>()

    fun addPurchase(purchase: Purchase) {
        purchases.add(0, purchase) // add to front
    }

    fun allPurchases(): List<Purchase> = purchases.toList()

    fun clear() {
        purchases.clear()
    }
}
