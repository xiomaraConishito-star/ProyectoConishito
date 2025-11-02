package com.cibertec.conishitoapp.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.conishitoapp.R

class PurchaseHistoryFragment : Fragment() {

    private lateinit var rvPurchases: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPurchases = view.findViewById(R.id.rvPurchases)
        val purchases = PurchaseManager.allPurchases()
        rvPurchases.layoutManager = LinearLayoutManager(requireContext())
        rvPurchases.adapter = PurchaseAdapter(purchases)
    }
}

