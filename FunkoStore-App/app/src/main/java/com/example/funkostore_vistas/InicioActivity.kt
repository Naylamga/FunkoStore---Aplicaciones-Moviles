package com.example.funkostore_vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class InicioActivity : AppCompatActivity() {

    private var webSocketClient: WebSocketClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bienvenido)

        val sessionManager = SessionManager(applicationContext)
        ApiClient.setToken(sessionManager.token)

        conectarWebSocket(sessionManager.token)

        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, ListaProductosActivity::class.java))
        }

        findViewById<Button>(R.id.button4).setOnClickListener {
            sessionManager.logout()
            webSocketClient?.disconnect()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun conectarWebSocket(token: String?) {
        webSocketClient = WebSocketClient(
            onConnected = {
                runOnUiThread {
                    Toast.makeText(this, "Conectado en tiempo real", Toast.LENGTH_SHORT).show()
                }
            },
            onStockUpdated = { pedidoId ->
                runOnUiThread {
                    Toast.makeText(this, "Stock actualizado (pedido #$pedidoId)", Toast.LENGTH_SHORT).show()
                }
            }
        )
        webSocketClient?.connect(token)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient?.disconnect()
    }
}
