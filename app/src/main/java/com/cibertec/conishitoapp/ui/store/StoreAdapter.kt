package com.cibertec.conishitoapp.ui.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.conishitoapp.R

class StoreAdapter(
    private val items: List<StoreItem>,
    private val onItemClick: (StoreItem) -> Unit
) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivItemImage)
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.ivImage.setImageResource(item.imageRes)
        holder.tvName.text = item.name
        holder.tvPrice.text = item.price
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
