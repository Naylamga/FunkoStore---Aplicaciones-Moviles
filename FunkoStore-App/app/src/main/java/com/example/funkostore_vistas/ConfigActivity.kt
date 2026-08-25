package com.example.funkostore_vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged

class ConfigActivity : AppCompatActivity() {

    private lateinit var edtIp: EditText
    private lateinit var edtPuerto: EditText
    private lateinit var edtEndpoint: EditText
    private lateinit var txtVistaPrevia: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        edtIp = findViewById(R.id.edtIp)
        edtPuerto = findViewById(R.id.edtPuerto)
        edtEndpoint = findViewById(R.id.edtEndpoint)
        txtVistaPrevia = findViewById(R.id.txtVistaPrevia)

        if (ConfiguracionApi.ip.isNotBlank()) edtIp.setText(ConfiguracionApi.ip)
        if (ConfiguracionApi.puerto.isNotBlank()) edtPuerto.setText(ConfiguracionApi.puerto)
        if (ConfiguracionApi.endpoint.isNotBlank()) edtEndpoint.setText(ConfiguracionApi.endpoint)

        actualizarVistaPrevia()
        edtIp.doAfterTextChanged { actualizarVistaPrevia() }
        edtPuerto.doAfterTextChanged { actualizarVistaPrevia() }
        edtEndpoint.doAfterTextChanged { actualizarVistaPrevia() }

        findViewById<Button>(R.id.btnContinuar).setOnClickListener { guardarYContinuar() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun actualizarVistaPrevia() {
        val ip = edtIp.text.toString().trim().ifEmpty { "10.0.2.2" }
        val puerto = edtPuerto.text.toString().trim().ifEmpty { "3000" }
        val endpoint = edtEndpoint.text.toString().trim().trimStart('/').ifEmpty { "api" }
        txtVistaPrevia.text = "ejemplo: http://$ip:$puerto/$endpoint/"
    }

    private fun guardarYContinuar() {
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
            endpoint = "api"
        }

        ConfiguracionApi.ip = ip
        ConfiguracionApi.puerto = puerto
        ConfiguracionApi.endpoint = endpoint.trimStart('/')
        ApiClient.resetClient()

        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
