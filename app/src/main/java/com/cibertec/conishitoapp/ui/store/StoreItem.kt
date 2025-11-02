package com.cibertec.conishitoapp.ui.store

@Suppress("unused")
data class StoreItem(
    val imageRes: Int,
    val name: String,
    val price: String,
    val description: String,
    val stock: Int,
    val colors: ArrayList<String>
)
