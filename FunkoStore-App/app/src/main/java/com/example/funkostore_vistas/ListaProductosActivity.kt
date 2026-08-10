package com.example.funkostore_vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ListaProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista_productos)

        recyclerView = findViewById(R.id.listaProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductoAdapter(
            onItemClick = { producto ->
                val intent = Intent(this, FormProductoActivity::class.java)
                intent.putExtra("id_producto", producto.id)
                startActivity(intent)
            },
            onItemLongClick = { producto ->
                AlertDialog.Builder(this)
                    .setTitle("Dar de baja")
                    .setMessage("¿Dar de baja \"${producto.nombre}\"?")
                    .setPositiveButton("Sí") { _, _ ->
                        lifecycleScope.launch {
                            val result = ProductoStore.darDeBajaProducto(producto.id)
                            result.onSuccess {
                                Toast.makeText(this@ListaProductosActivity, "Producto dado de baja", Toast.LENGTH_SHORT).show()
                                cargarLista()
                            }.onFailure {
                                Toast.makeText(this@ListaProductosActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
                true
            },
        )
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnAgregar).setOnClickListener {
            startActivity(Intent(this, FormProductoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLista()
    }

    private fun cargarLista() {
        lifecycleScope.launch {
            val result = ProductoStore.obtenerProductosActivos()
            result.onSuccess { productos ->
                adapter.actualizarLista(productos)
            }.onFailure {
                Toast.makeText(this@ListaProductosActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
