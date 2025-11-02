package com.cibertec.conishitoapp.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cibertec.conishitoapp.R

class ProductDetailFragment : Fragment() {

    companion object {
        const val ARG_IMAGE = "arg_image"
        const val ARG_NAME = "arg_name"
        const val ARG_PRICE = "arg_price"
        const val ARG_DESCRIPTION = "arg_description"
        const val ARG_STOCK = "arg_stock"
        const val ARG_COLORS = "arg_colors"

        fun newInstance(item: StoreItem): ProductDetailFragment {
            val frag = ProductDetailFragment()
            val b = Bundle()
            b.putInt(ARG_IMAGE, item.imageRes)
            b.putString(ARG_NAME, item.name)
            b.putString(ARG_PRICE, item.price)
            b.putString(ARG_DESCRIPTION, item.description)
            b.putInt(ARG_STOCK, item.stock)
            b.putStringArrayList(ARG_COLORS, item.colors)
            frag.arguments = b
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivImage = view.findViewById<ImageView>(R.id.ivProductImage)
        val tvName = view.findViewById<TextView>(R.id.tvProductName)
        val tvPrice = view.findViewById<TextView>(R.id.tvProductPrice)
        val tvStock = view.findViewById<TextView>(R.id.tvProductStock)
        val tvDescription = view.findViewById<TextView>(R.id.tvProductDescription)
        val tvColors = view.findViewById<TextView>(R.id.tvProductColors)
        val btnAdd = view.findViewById<Button>(R.id.btnAddToCart)

        var currentItem: StoreItem? = null

        arguments?.let { b ->
            ivImage.setImageResource(b.getInt(ARG_IMAGE, R.drawable.ic_store_24))
            tvName.text = b.getString(ARG_NAME, "")
            tvPrice.text = b.getString(ARG_PRICE, "")
            tvStock.text = getString(R.string.product_stock, b.getInt(ARG_STOCK, 0))
            tvDescription.text = b.getString(ARG_DESCRIPTION, "")
            val colors = b.getStringArrayList(ARG_COLORS) ?: arrayListOf()
            tvColors.text = colors.joinToString(", ")

            currentItem = StoreItem(
                b.getInt(ARG_IMAGE, R.drawable.ic_store_24),
                b.getString(ARG_NAME, "") ?: "",
                b.getString(ARG_PRICE, "") ?: "",
                b.getString(ARG_DESCRIPTION, "") ?: "",
                b.getInt(ARG_STOCK, 0),
                colors
            )
        }

        btnAdd.setOnClickListener {
            currentItem?.let { item ->
                CartManager.addProduct(item, 1)
                Toast.makeText(requireContext(), getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
