package com.example.funkostore_vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CrearCuentaActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_cuenta)

        sessionManager = SessionManager(applicationContext)

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnCrear).setOnClickListener {
            val nombre = findViewById<EditText>(R.id.txtNombre).text.toString().trim()
            val apellido = findViewById<EditText>(R.id.txtApellido).text.toString().trim()
            val email = findViewById<EditText>(R.id.txtEmail).text.toString().trim()
            val pass = findViewById<EditText>(R.id.txtPassw).text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = ApiClient.apiService.register(
                    RegisterRequest(nombre, apellido, email, pass)
                )
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
                    }

                    Toast.makeText(this@CrearCuentaActivity, "Cuenta creada. Bienvenido!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@CrearCuentaActivity, InicioActivity::class.java))
                    finish()
                } else {
                    val msg = result.body()?.message ?: "Error al crear cuenta"
                    Toast.makeText(this@CrearCuentaActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
