package com.example.funkostore_vistas

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class FormProductoActivity : AppCompatActivity() {

    private var idProducto = -1
    private var rutaImagen = ""

    private var franquicias = listOf<FranquiciaData>()
    private var proveedores = listOf<ProveedorData>()

    private lateinit var imgPreview: ImageView

    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            guardarImagenSeleccionada(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_producto)

        idProducto = intent.getIntExtra("id_producto", -1)
        imgPreview = findViewById(R.id.imgPreview)

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardar() }
        findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        val titulo = findViewById<TextView>(R.id.txtTituloForm)
        if (idProducto == -1) {
            titulo.text = "Alta de Funko"
            imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
        } else {
            titulo.text = "Modificar Funko"
        }

        cargarSpinners()
    }

    private fun cargarSpinners() {
        lifecycleScope.launch {
            val franquiciasResult = ProductoStore.obtenerFranquicias()
            val proveedoresResult = ProductoStore.obtenerProveedores()

            franquiciasResult.onSuccess { franquicias = it }
            proveedoresResult.onSuccess { proveedores = it }

            findViewById<Spinner>(R.id.spinnerFranquicia).adapter = ArrayAdapter(
                this@FormProductoActivity,
                android.R.layout.simple_spinner_dropdown_item,
                franquicias.map { it.nombreFranquicia }
            )
            findViewById<Spinner>(R.id.spinnerProveedor).adapter = ArrayAdapter(
                this@FormProductoActivity,
                android.R.layout.simple_spinner_dropdown_item,
                proveedores.map { it.nombreProveedor }
            )

            if (idProducto != -1) {
                cargarDatos()
            }
        }
    }

    private fun guardarImagenSeleccionada(uri: Uri) {
        try {
            val input = contentResolver.openInputStream(uri)
            if (input == null) {
                Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show()
                return
            }

            val carpeta = File(filesDir, "imagenes")
            if (!carpeta.exists()) carpeta.mkdirs()

            val archivo = File(carpeta, "funko_${System.currentTimeMillis()}.jpg")
            FileOutputStream(archivo).use { output ->
                input.use { it.copyTo(output) }
            }

            rutaImagen = archivo.absolutePath
            imgPreview.setImageURI(Uri.fromFile(archivo))
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun cargarDatos() {
        val result = ProductoStore.obtenerProductoPorId(idProducto)
        result.onSuccess { p ->
            if (p == null) return@onSuccess

            findViewById<EditText>(R.id.txtNombre).setText(p.nombreProducto)
            findViewById<EditText>(R.id.txtDescripcion).setText(p.descripcion ?: "")
            findViewById<EditText>(R.id.txtPrecio).setText(p.precio.toString())
            findViewById<EditText>(R.id.txtStock).setText(p.stock.toString())
            findViewById<EditText>(R.id.txtCodigoBarra).setText(p.codigoBarra ?: "")

            rutaImagen = p.urlImagen
            if (rutaImagen.isNotEmpty()) {
                imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            val idxFranquicia = franquicias.indexOfFirst { it.idFranquicia == p.idFranquicia }
            if (idxFranquicia >= 0) {
                findViewById<Spinner>(R.id.spinnerFranquicia).setSelection(idxFranquicia)
            }

            val idxProveedor = proveedores.indexOfFirst { it.idProveedor == p.idProveedor }
            if (idxProveedor >= 0) {
                findViewById<Spinner>(R.id.spinnerProveedor).setSelection(idxProveedor)
            }
        }
    }

    private fun guardar() {
        val nombre = findViewById<EditText>(R.id.txtNombre).text.toString().trim()
        val descripcion = findViewById<EditText>(R.id.txtDescripcion).text.toString().trim()
        val precioStr = findViewById<EditText>(R.id.txtPrecio).text.toString().trim()
        val stockStr = findViewById<EditText>(R.id.txtStock).text.toString().trim()
        val codigoBarra = findViewById<EditText>(R.id.txtCodigoBarra).text.toString().trim()

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Nombre, precio y stock son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        val stock = stockStr.toIntOrNull()

        if (precio == null || stock == null) {
            Toast.makeText(this, "Precio y stock deben ser numéricos", Toast.LENGTH_SHORT).show()
            return
        }

        val idxFranquicia = findViewById<Spinner>(R.id.spinnerFranquicia).selectedItemPosition
        val idxProveedor = findViewById<Spinner>(R.id.spinnerProveedor).selectedItemPosition

        val idFranquicia = franquicias.getOrNull(idxFranquicia)?.idFranquicia ?: 1
        val idProveedor = proveedores.getOrNull(idxProveedor)?.idProveedor ?: 1

        lifecycleScope.launch {
            if (idProducto == -1) {
                val result = ProductoStore.crearProducto(
                    nombre, descripcion, precio, stock, codigoBarra,
                    idFranquicia, idProveedor, rutaImagen
                )
                result.onSuccess {
                    Toast.makeText(this@FormProductoActivity, "Funko agregado", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure {
                    Toast.makeText(this@FormProductoActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                val result = ProductoStore.actualizarProducto(
                    idProducto, nombre, descripcion, precio, stock, codigoBarra,
                    idFranquicia, idProveedor, rutaImagen
                )
                result.onSuccess {
                    Toast.makeText(this@FormProductoActivity, "Funko actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure {
                    Toast.makeText(this@FormProductoActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
