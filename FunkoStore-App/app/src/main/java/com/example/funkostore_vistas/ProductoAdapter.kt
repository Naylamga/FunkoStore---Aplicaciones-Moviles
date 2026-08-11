package com.example.funkostore_vistas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductoAdapter(
    private var items: List<Producto> = emptyList(),
    private val onEdit: (Producto) -> Unit,
    private val onBaja: (Producto) -> Unit,
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    fun actualizarLista(nuevosItems: List<Producto>) {
        items = nuevosItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(items[position], onEdit, onBaja)
    }

    override fun getItemCount() = items.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val txtProducto: TextView = itemView.findViewById(R.id.txtProducto)
        private val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        private val btnBaja: Button = itemView.findViewById(R.id.btnBaja)

        fun bind(
            producto: Producto,
            onEdit: (Producto) -> Unit,
            onBaja: (Producto) -> Unit
        ) {
            txtProducto.text =
                "${producto.nombre}\nPrecio: $${producto.precio} | Stock: ${producto.stock} | ${producto.franquicia}"

            val context = itemView.context
            if (producto.urlImagen.isNotEmpty()) {
                Glide.with(context)
                    .load(producto.urlImagen)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(imgProducto)
            } else {
                imgProducto.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            btnEditar.setOnClickListener { onEdit(producto) }
            btnBaja.setOnClickListener { onBaja(producto) }
            itemView.setOnClickListener { onEdit(producto) }
        }
    }
}
