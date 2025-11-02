package com.cibertec.conishitoapp.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.conishitoapp.R

class CartFragment : Fragment() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvCart = view.findViewById(R.id.rvCart)
        tvTotal = view.findViewById(R.id.tvCartTotal)
        btnCheckout = view.findViewById(R.id.btnCheckout)

        loadCart()

        adapter = CartAdapter(cartItems, onRemove = { item ->
            CartManager.removeProduct(item.product)
            loadCart()
        }, onQuantityChanged = { item, qty ->
            CartManager.updateQuantity(item.product, qty)
            loadCart()
        })

        rvCart.layoutManager = LinearLayoutManager(requireContext())
        rvCart.adapter = adapter

        btnCheckout.setOnClickListener {
            // Abrir pantalla de checkout usando nombre de paquete completo
            val checkout = com.cibertec.conishitoapp.ui.store.CheckoutFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, checkout)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadCart() {
        cartItems.clear()
        cartItems.addAll(CartManager.allItems())
        adapter = if (::adapter.isInitialized) adapter else CartAdapter(cartItems, onRemove = { item ->
            CartManager.removeProduct(item.product)
            loadCart()
        }, onQuantityChanged = { item, qty ->
            CartManager.updateQuantity(item.product, qty)
            loadCart()
        })
        rvCart.adapter = adapter
        tvTotal.text = getString(R.string.cart_total, CartManager.total())
    }
}
