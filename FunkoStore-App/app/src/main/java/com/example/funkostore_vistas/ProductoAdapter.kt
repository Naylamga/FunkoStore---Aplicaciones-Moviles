package com.example.funkostore_vistas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductoAdapter(
    private var items: List<Producto> = emptyList(),
    private val onItemClick: (Producto) -> Unit,
    private val onItemLongClick: (Producto) -> Boolean,
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
        val producto = items[position]
        holder.bind(producto)
        holder.itemView.setOnClickListener { onItemClick(producto) }
        holder.itemView.setOnLongClickListener { onItemLongClick(producto) }
    }

    override fun getItemCount() = items.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val txtProducto: TextView = itemView.findViewById(R.id.txtProducto)

        fun bind(producto: Producto) {
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
        }
    }
}
