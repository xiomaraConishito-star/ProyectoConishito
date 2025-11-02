package com.cibertec.conishitoapp.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.conishitoapp.R

class StoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lista de ejemplo con items que contienen imagen, nombre, precio, descripción, stock y colores
        val items = listOf(
            StoreItem(
                R.drawable.collar,
                "Collar para perro",
                "S/ 25.00",
                "Collar ajustable de nylon, ideal para perros pequeños y medianos.",
                12,
                arrayListOf("Rojo", "Azul", "Negro")
            ),
            StoreItem(
                R.drawable.peluche,
                "Juguete de peluche",
                "S/ 35.00",
                "Peluche con material suave, resistente a mordidas suaves.",
                8,
                arrayListOf("Marrón", "Blanco")
            ),
            StoreItem(
                R.drawable.comidag,
                "Comida para gato",
                "S/ 45.00",
                "Bolsa 1kg, fórmula balanceada para gatos adultos.",
                20,
                arrayListOf("N/A")
            ),
            StoreItem(
                R.drawable.camag,
                "Camita pequeña",
                "S/ 80.00",
                "Camita acolchada para mascotas pequeñas, lavable.",
                4,
                arrayListOf("Gris", "Beige")
            ),
            StoreItem(
                R.drawable.correa,
                "Correa retráctil",
                "S/ 50.00",
                "Correa con freno y mosquetón metálico, hasta 5m.",
                15,
                arrayListOf("Negro")
            ),
            StoreItem(
                R.drawable.plato,
                "Plato de comida",
                "S/ 15.00",
                "Plástico resistente y anti-deslizante.",
                30,
                arrayListOf("Azul", "Rosa")
            )
        )

        val rvStore = view.findViewById<RecyclerView>(R.id.rvStore)
        rvStore.layoutManager = GridLayoutManager(requireContext(), 2)
        rvStore.adapter = StoreAdapter(items) { selectedItem ->
            // Abrir fragmento de detalle
            val detail = ProductDetailFragment.newInstance(selectedItem)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, detail)
                .addToBackStack(null)
                .commit()
        }
    }
}
