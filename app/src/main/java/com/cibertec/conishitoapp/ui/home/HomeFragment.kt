package com.cibertec.conishitoapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cibertec.conishitoapp.R
import com.cibertec.conishitoapp.data.Pet
import com.cibertec.conishitoapp.data.local.DatabaseRepository
import com.cibertec.conishitoapp.data.local.SessionManager
import com.cibertec.conishitoapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { DatabaseRepository(requireContext()) }
    private val sessionManager by lazy { SessionManager(requireContext()) }
    private lateinit var adapter: PetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PetAdapter(
            onClick = { openDetail(it) },
            onToggleFavorite = { toggleFavorite(it) }
        )
        binding.rvPets.adapter = adapter
        binding.rvPets.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.home_title)
        loadPets()
    }

    private fun loadPets() {
        val userId = sessionManager.getUserId()
        viewLifecycleOwner.lifecycleScope.launch {
            val pets = withContext(Dispatchers.IO) { repository.getPets(userId) }
            adapter.submitList(pets)
        }
    }

    private fun toggleFavorite(pet: Pet) {
        val userId = sessionManager.getUserId()
        if (userId == null) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (pet.isFavorite) {
                    repository.removeFavorite(userId, pet.id)
                } else {
                    repository.addFavorite(userId, pet.id)
                }
            }
            loadPets()
        }
    }

    private fun openDetail(pet: Pet) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PetDetailFragment.newInstance(pet.id))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
