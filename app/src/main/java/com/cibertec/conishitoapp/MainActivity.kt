package com.cibertec.conishitoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cibertec.conishitoapp.data.FakePets
import com.cibertec.conishitoapp.data.Pet
import com.cibertec.conishitoapp.ui.theme.screens.UserLoginXmlScreen
import com.cibertec.conishitoapp.ui.theme.screens.UserRegisterXmlScreen


class MainActivity : ComponentActivity() {
    private val splashVm: SplashViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash: SplashScreen = installSplashScreen()
        splash.setKeepOnScreenCondition { splashVm.isLoading }
        super.onCreate(savedInstanceState)

        setContent {
            // Cambia por tu theme si se llama distinto
            MaterialTheme {
                var selectedTab by remember { mutableStateOf(BottomTab.Home) }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    topBar = {
                        CenterAlignedTopAppBar(title = {
                            Text(
                                when (selectedTab) {
                                    BottomTab.Home -> "Mascotas buscando Hogar"
                                    BottomTab.Favorites -> "Mis favoritos"
                                    BottomTab.User -> "Usuario"
                                }
                            )
                        })
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == BottomTab.User,
                                onClick = { selectedTab = BottomTab.User },
                                icon = { Icon(Icons.Outlined.Person, contentDescription = "Usuario") },
                                label = { Text("User") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == BottomTab.Home,
                                onClick = { selectedTab = BottomTab.Home },
                                icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == BottomTab.Favorites,
                                onClick = { selectedTab = BottomTab.Favorites },
                                icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Favoritos") },
                                label = { Text("Favoritos") }
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets(0)
                ) { inner ->
                    // Estado local para alternar Login <-> Registro dentro del tab User
                    var showRegister by rememberSaveable { mutableStateOf(false) }

                    when (selectedTab) {
                        BottomTab.Home -> {
                            HomeListContent(
                                modifier = Modifier
                                    .padding(inner)
                                    .fillMaxSize()
                            )
                        }

                        BottomTab.User -> {
                            if (showRegister) {
                                // Pantalla REGISTRO (XML)
                                UserRegisterXmlScreen(
                                    modifier = Modifier
                                        .padding(inner)
                                        .fillMaxSize(),
                                    onCreateAccount = { name, email, pass, phone, city ->
                                        // TODO: crear cuenta (validar/llamar backend)
                                    },
                                    onBackToLogin = {
                                        showRegister = false     // ← volver a Login
                                    }
                                )
                            } else {
                                // Pantalla LOGIN (XML)
                                UserLoginXmlScreen(
                                    modifier = Modifier
                                        .padding(inner)
                                        .fillMaxSize(),
                                    onLogin = { email, pass ->
                                        // TODO: login (validar/llamar backend)
                                    },
                                    onForgot = {
                                        // TODO: ir a recuperación (otra pantalla si quieres)
                                    },
                                    onRegister = {
                                        showRegister = true      // ← ir a Registro
                                    }
                                )
                            }
                        }

                        BottomTab.Favorites -> {
                            Box(
                                modifier = Modifier
                                    .padding(inner)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Pantalla Favoritos (...)")
                            }
                        }
                    }
                }

            }
        }
    }
}

private enum class BottomTab { User, Home, Favorites }

@Composable
private fun HomeListContent(modifier: Modifier = Modifier) {
    val pets = remember { FakePets.items }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pets, key = { it.id }) { pet ->
            PetCard(pet = pet, onClick = {
                // Aquí luego navegaremos al detalle
            })
        }
    }
}

@Composable
private fun PetCard(pet: Pet, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder de imagen (sin Coil, para no añadir deps extra aquí)
            Surface(
                modifier = Modifier.size(56.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {}

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(pet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${pet.type} • ${pet.age}", style = MaterialTheme.typography.bodyMedium)
                Text(pet.city, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}