package com.cibertec.conishitoapp.ui.theme.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cibertec.conishitoapp.navigation.Route

@Composable
fun BottomBar(navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination

    NavigationBar {
        NavigationBarItem(
            selected = current.isOn(Route.Home.path),
            onClick = { navController.navigate(Route.Home.path) { launchSingleTop = true; popUpTo(Route.Home.path) { inclusive = false } } },
            icon = { Icon(Icons.Outlined.Home, null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = current.isOn(Route.Favorites.path),
            onClick = { navController.navigate(Route.Favorites.path) { launchSingleTop = true } },
            icon = { Icon(Icons.Outlined.Favorite, null) },
            label = { Text("Favoritos") }
        )
        NavigationBarItem(
            selected = current.isOn(Route.User.path),
            onClick = { navController.navigate(Route.User.path) { launchSingleTop = true } },
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text("Usuario") }
        )
    }
}

private fun NavDestination?.isOn(route: String): Boolean =
    this?.hierarchy?.any { it.route == route } == true