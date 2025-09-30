package com.cibertec.conishitoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    // Controla la duración del splash (simulamos carga inicial)
    var isLoading: Boolean = true
        private set

    init {
        viewModelScope.launch {
            // Simula carga de configuración/servicios (1.3 s)
            delay(1300)
            isLoading = false
        }
    }
}