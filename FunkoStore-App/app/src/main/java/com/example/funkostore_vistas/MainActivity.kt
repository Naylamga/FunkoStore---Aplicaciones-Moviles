package com.example.funkostore_vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged

class MainActivity : AppCompatActivity() {

    private lateinit var edtIp: EditText
    private lateinit var edtPuerto: EditText
    private lateinit var edtEndpoint: EditText
    private lateinit var txtVistaPrevia: TextView
    private lateinit var btnContinuar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        edtIp = findViewById(R.id.edtIp)
        edtPuerto = findViewById(R.id.edtPuerto)
        edtEndpoint = findViewById(R.id.edtEndpoint)
        txtVistaPrevia = findViewById(R.id.txtVistaPrevia)
        btnContinuar = findViewById(R.id.btnContinuar)

        actualizarVistaPrevia()
        edtIp.doAfterTextChanged { actualizarVistaPrevia() }
        edtPuerto.doAfterTextChanged { actualizarVistaPrevia() }
        edtEndpoint.doAfterTextChanged { actualizarVistaPrevia() }

        btnContinuar.setOnClickListener { guardarConfiguracion() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun actualizarVistaPrevia() {
        val ip = edtIp.text.toString().trim().ifEmpty { "10.0.2.2" }
        val puerto = edtPuerto.text.toString().trim().ifEmpty { "3000" }
        val endpoint = edtEndpoint.text.toString().trim().trimStart('/').ifEmpty { "productos" }
        txtVistaPrevia.text = "ejemplo: http://$ip:$puerto/$endpoint"
    }

    private fun guardarConfiguracion() {
        val ip = edtIp.text.toString().trim()
        val puerto = edtPuerto.text.toString().trim()
        var endpoint = edtEndpoint.text.toString().trim()

        if (ip.isEmpty()) {
            edtIp.error = "Ingrese la IP"
            edtIp.requestFocus()
            return
        }
        if (puerto.isEmpty()) {
            edtPuerto.error = "Ingrese el puerto"
            edtPuerto.requestFocus()
            return
        }
        if (endpoint.isEmpty()) {
            edtEndpoint.error = "Ingrese el endpoint"
            edtEndpoint.requestFocus()
            return
        }

        endpoint = endpoint.trimStart('/')
        ConfiguracionApi.ip = ip
        ConfiguracionApi.puerto = puerto
        ConfiguracionApi.endpoint = endpoint

        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, ProductoActivity::class.java))
    }
}
