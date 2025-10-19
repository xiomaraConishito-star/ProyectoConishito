package com.cibertec.conishitoapp.data

data class User(
    val id: Long = 0L,
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String?,
    val city: String?
)
