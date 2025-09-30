package com.cibertec.conishitoapp.data

data class Pet(
    val id: String,
    val name: String,
    val type: String,   // "Perro" | "Gato"
    val age: String,    // "4 meses", "1 año"
    val city: String,   // "Lima", "Arequipa"
    val photoUrl: String? = null
)