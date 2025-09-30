package com.cibertec.conishitoapp.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Favorites : Route("favorites")
    data object User : Route("user")
    data object Detail : Route("detail/{petId}") {
        fun create(petId: String) = "detail/$petId"
    }
}