package com.example.funkostore_vistas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoActivity : AppCompatActivity() {

    private lateinit var edtProducto: EditText
    private lateinit var edtMarca: EditText
    private lateinit var edtDescripcion: EditText
    private lateinit var btnEnviar: Button
    private lateinit var btnCambiarConfiguracion: Button
    private lateinit var txtResultado: TextView
    private lateinit var txtDireccionApi: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        edtProducto = findViewById(R.id.edtProducto)
        edtMarca = findViewById(R.id.edtMarca)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnCambiarConfiguracion = findViewById(R.id.btnCambiarConfiguracion)
        txtResultado = findViewById(R.id.txtResultado)
        txtDireccionApi = findViewById(R.id.txtDireccionApi)
        progressBar = findViewById(R.id.progressBar)

        if (!ConfiguracionApi.configuracionCompleta()) {
            volverAConfiguracion()
            return
        }

        mostrarDireccionApi()
        btnEnviar.setOnClickListener { validarYEnviar() }
        btnCambiarConfiguracion.setOnClickListener { volverAConfiguracion() }
    }

    private fun mostrarDireccionApi() {
        val direccionCompleta =
            ConfiguracionApi.obtenerUrlBase() + ConfiguracionApi.endpoint
        txtDireccionApi.text = "Destino: $direccionCompleta"
    }

    private fun validarYEnviar() {
        val nombreProducto = edtProducto.text.toString().trim()
        val marca = edtMarca.text.toString().trim()
        val descripcion = edtDescripcion.text.toString().trim()

        if (nombreProducto.isEmpty()) {
            edtProducto.error = "Ingrese el nombre del producto"
            edtProducto.requestFocus()
            return
        }
        if (marca.isEmpty()) {
            edtMarca.error = "Ingrese la marca"
            edtMarca.requestFocus()
            return
        }
        if (descripcion.isEmpty()) {
            edtDescripcion.error = "Ingrese la descripción"
            edtDescripcion.requestFocus()
            return
        }

        enviarProducto(
            Producto(
                producto = nombreProducto,
                marca = marca,
                descripcion = descripcion
            )
        )
    }

    private fun enviarProducto(producto: Producto) {
        progressBar.visibility = View.VISIBLE
        btnEnviar.isEnabled = false
        txtResultado.text = "Enviando producto..."

        val servicio = RetrofitCliente.crearServicio()
        val llamada = servicio.enviarProducto(
            endpoint = ConfiguracionApi.endpoint,
            producto = producto
        )

        llamada.enqueue(object : Callback<RespuestaApi> {
            override fun onResponse(
                call: Call<RespuestaApi>,
                response: Response<RespuestaApi>
            ) {
                progressBar.visibility = View.GONE
                btnEnviar.isEnabled = true

                if (response.isSuccessful) {
                    val respuesta = response.body()
                    val mensaje = respuesta?.mensaje ?: "Producto enviado correctamente"
                    txtResultado.text = mensaje
                    Toast.makeText(this@ProductoActivity, mensaje, Toast.LENGTH_LONG).show()
                    limpiarFormulario()
                } else {
                    val errorBody = response.errorBody()?.string()
                    txtResultado.text = "Error del servidor: ${response.code()} $errorBody"
                    Toast.makeText(
                        this@ProductoActivity,
                        "La API respondió con el código ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<RespuestaApi>, throwable: Throwable) {
                progressBar.visibility = View.GONE
                btnEnviar.isEnabled = true
                txtResultado.text = "Error de conexión: ${throwable.message}"
                Toast.makeText(
                    this@ProductoActivity,
                    "No se pudo conectar con la API",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun limpiarFormulario() {
        edtProducto.text.clear()
        edtMarca.text.clear()
        edtDescripcion.text.clear()
        edtProducto.requestFocus()
    }

    private fun volverAConfiguracion() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
