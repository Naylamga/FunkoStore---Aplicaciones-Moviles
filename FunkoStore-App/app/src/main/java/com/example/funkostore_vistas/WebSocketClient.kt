package com.example.funkostore_vistas

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URI

class WebSocketClient(
    private val onStockUpdated: ((pedidoId: Int) -> Unit)? = null,
    private val onNewOrder: ((pedidoId: Int) -> Unit)? = null,
    private val onConnected: (() -> Unit)? = null,
    private val onDisconnected: (() -> Unit)? = null
) {

    private var socket: Socket? = null

    fun connect(token: String?) {
        try {
            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 2000
                timeout = 5000
                auth = mapOf("token" to (token ?: ""))
            }

            socket = IO.socket(URI.create("http://10.0.2.2:3000"), options)

            socket?.on(Socket.EVENT_CONNECT) {
                onConnected?.invoke()
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                onDisconnected?.invoke()
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                // No tumbar la app si la API no está
                android.util.Log.w("WebSocketClient", "connect_error: ${args.firstOrNull()}")
            }

            socket?.on("stock:updated") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as? JSONObject
                    val pedidoId = data?.optInt("pedidoId") ?: return@on
                    onStockUpdated?.invoke(pedidoId)
                }
            }

            socket?.on("order:new") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as? JSONObject
                    val pedidoId = data?.optInt("pedidoId") ?: return@on
                    onNewOrder?.invoke(pedidoId)
                }
            }

            socket?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
    }

    val isConnected: Boolean get() = socket?.connected() ?: false
}
