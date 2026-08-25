package com.example.funkostore_vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        sessionManager = SessionManager(applicationContext)

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnIrCrear).setOnClickListener {
            startActivity(Intent(this, CrearCuentaActivity::class.java))
        }

        findViewById<Button>(R.id.btnIniciar).setOnClickListener {
            val email = findViewById<EditText>(R.id.txtEmail).text.toString().trim()
            val pass = findViewById<EditText>(R.id.txtPassw).text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val result = ApiClient.apiService.login(LoginRequest(email, pass))
                    if (result.isSuccessful && result.body()?.success == true) {
                        val authData = result.body()?.data
                        val token = authData?.token
                        val usuario = authData?.usuario

                        if (token != null) {
                            sessionManager.token = token
                            sessionManager.userEmail = usuario?.email
                            sessionManager.userRole = usuario?.rol
                            sessionManager.userId = usuario?.id ?: -1
                            ApiClient.setToken(token)

                            Toast.makeText(this@LoginActivity, "Bienvenido ${usuario?.nombre ?: ""}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, InicioActivity::class.java))
                            finish()
                        }
                    } else {
                        val msg = result.body()?.message ?: "Email o contraseña incorrectos"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "No se pudo conectar con el servidor: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
