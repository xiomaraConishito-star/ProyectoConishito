package com.cibertec.conishitoapp.ui.fav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cibertec.conishitoapp.MainActivity
import com.cibertec.conishitoapp.R
import com.cibertec.conishitoapp.data.Pet
import com.cibertec.conishitoapp.data.local.DatabaseRepository
import com.cibertec.conishitoapp.data.local.SessionManager
import com.cibertec.conishitoapp.databinding.FragmentFavoritesBinding
import com.cibertec.conishitoapp.ui.home.PetAdapter
import com.cibertec.conishitoapp.ui.home.PetDetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { DatabaseRepository(requireContext()) }
    private val sessionManager by lazy { SessionManager(requireContext()) }
    private lateinit var adapter: PetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PetAdapter(
            onClick = { openDetail(it) },
            onToggleFavorite = { toggleFavorite(it) }
        )
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.setHasFixedSize(true)

        binding.btnGoLogin.setOnClickListener {
            (requireActivity() as? MainActivity)?.navigateTo(R.id.nav_user, shouldCheck = true)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.favorites_title)
        loadFavorites()
    }

    private fun loadFavorites() {
        val userId = sessionManager.getUserId()
        if (userId == null) {
            adapter.submitList(emptyList())
            binding.emptyState.isVisible = true
            binding.btnGoLogin.isVisible = true
            binding.tvEmptyState.setText(R.string.favorites_guest)
            binding.rvFavorites.isVisible = false
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val favorites = withContext(Dispatchers.IO) { repository.getFavorites(userId) }
            adapter.submitList(favorites)
            binding.emptyState.isVisible = favorites.isEmpty()
            binding.btnGoLogin.isVisible = false
            binding.tvEmptyState.setText(R.string.favorites_empty)
            binding.rvFavorites.isVisible = favorites.isNotEmpty()
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
                repository.removeFavorite(userId, pet.id)
            }
            loadFavorites()
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
