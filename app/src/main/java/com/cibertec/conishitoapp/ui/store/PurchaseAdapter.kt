package com.cibertec.conishitoapp.ui.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.conishitoapp.R
import java.text.DateFormat
import java.util.Date

class PurchaseAdapter(private val items: List<Purchase>) : RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvPurchaseDate)
        val tvSummary: TextView = view.findViewById(R.id.tvPurchaseSummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_purchase, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = items[position]
        val df = DateFormat.getDateTimeInstance()
        holder.tvDate.text = df.format(Date(p.date))
        holder.tvSummary.text = holder.itemView.context.getString(R.string.purchase_summary, p.items.size, p.total)
    }

    override fun getItemCount(): Int = items.size
}
