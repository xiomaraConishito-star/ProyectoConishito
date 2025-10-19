package com.cibertec.conishitoapp.data

data class Pet(
    val id: Long,
    val name: String,
    val type: String,
    val age: String,
    val city: String,
    val photoUrl: String? = null,
    val description: String = "",
    val contactPhone: String? = null,
    val isFavorite: Boolean = false
)