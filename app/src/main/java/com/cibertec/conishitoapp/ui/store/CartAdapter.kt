package com.cibertec.conishitoapp.ui.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.conishitoapp.R

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onRemove: (CartItem) -> Unit,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivCartImage)
        val tvName: TextView = view.findViewById(R.id.tvCartName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartPrice)
        val tvQty: TextView = view.findViewById(R.id.tvCartQty)
        val btnRemove: Button = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.ivImage.setImageResource(item.product.imageRes)
        holder.tvName.text = item.product.name
        holder.tvPrice.text = item.product.price
        holder.tvQty.text = holder.itemView.context.getString(R.string.qty, item.quantity)

        holder.btnRemove.setOnClickListener {
            onRemove(item)
            notifyItemRemoved(position)
        }

        // Para simplicidad, clic en la cantidad incrementa en 1
        holder.tvQty.setOnClickListener {
            item.quantity += 1
            onQuantityChanged(item, item.quantity)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = items.size
}

