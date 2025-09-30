package com.cibertec.conishitoapp.ui.theme.screens

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.cibertec.conishitoapp.R

@Composable
fun UserLoginXmlScreen(
    modifier: Modifier = Modifier,
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onForgot: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // Forzamos tema Material3 para este XML:
            val themed = ContextThemeWrapper(context, R.style.Theme_ConishitoApp)

            val v = LayoutInflater.from(themed).inflate(R.layout.screen_login, null, false)

            val etEmail = v.findViewById<TextInputEditText>(R.id.etEmail)
            val etPassword = v.findViewById<TextInputEditText>(R.id.etPassword)
            val btnLogin = v.findViewById<MaterialButton>(R.id.btnLogin)
            val tvForgot = v.findViewById<TextView>(R.id.tvForgot)
            val tvRegister = v.findViewById<TextView>(R.id.tvRegister)

            btnLogin.setOnClickListener {
                onLogin(etEmail.text?.toString().orEmpty(), etPassword.text?.toString().orEmpty())
            }
            tvForgot.setOnClickListener { onForgot() }
            tvRegister.setOnClickListener { onRegister() }

            v
        }
    )
}