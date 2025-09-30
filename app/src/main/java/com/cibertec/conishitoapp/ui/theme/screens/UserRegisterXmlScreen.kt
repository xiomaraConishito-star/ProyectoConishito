package com.cibertec.conishitoapp.ui.theme.screens

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.ArrayAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.cibertec.conishitoapp.R

@Composable
fun UserRegisterXmlScreen(
    modifier: Modifier = Modifier,
    onCreateAccount: (name: String, email: String, pass: String, phone: String?, city: String?) -> Unit = { _,_,_,_,_ -> },
    onBackToLogin: () -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val themed = ContextThemeWrapper(context, R.style.Theme_ConishitoApp)
            val v = LayoutInflater.from(themed).inflate(R.layout.screen_register, null, false)

            val etName = v.findViewById<TextInputEditText>(R.id.etName)
            val etEmail = v.findViewById<TextInputEditText>(R.id.etEmail)
            val etPassword = v.findViewById<TextInputEditText>(R.id.etPassword)
            val etConfirm = v.findViewById<TextInputEditText>(R.id.etConfirm)
            val etPhone = v.findViewById<TextInputEditText>(R.id.etPhone)
            val actCity = v.findViewById<AutoCompleteTextView>(R.id.actCity)
            val btnCreate = v.findViewById<MaterialButton>(R.id.btnCreateAccount)
            val tvGoLogin = v.findViewById<TextView>(R.id.tvGoLogin)

            // Opcional: datos de ciudades de ejemplo
            val ciudades = listOf("Lima","Arequipa","Cusco","Trujillo","Piura")
            actCity.setAdapter(ArrayAdapter(themed, android.R.layout.simple_list_item_1, ciudades))

            btnCreate.setOnClickListener {
                val name = etName.text?.toString().orEmpty()
                val email = etEmail.text?.toString().orEmpty()
                val pass = etPassword.text?.toString().orEmpty()
                val confirm = etConfirm.text?.toString().orEmpty()
                if (pass == confirm) {
                    onCreateAccount(name, email, pass, etPhone.text?.toString(), actCity.text?.toString())
                } else {
                    etConfirm.error = "Las contraseñas no coinciden"
                }
            }

            tvGoLogin.setOnClickListener { onBackToLogin() }

            v
        }
    )
}