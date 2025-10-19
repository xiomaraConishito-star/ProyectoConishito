package com.cibertec.conishitoapp.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import com.cibertec.conishitoapp.databinding.FragmentPetDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PetDetailFragment : Fragment() {

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { DatabaseRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val petId = requireArguments().getLong(ARG_PET_ID)
        loadPet(petId)
    }

    private fun loadPet(petId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val pet = withContext(Dispatchers.IO) { repository.getPetById(petId) }
            if (pet != null) {
                showPet(pet)
            } else {
                Toast.makeText(requireContext(), R.string.pet_not_found, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun showPet(pet: Pet) {
        binding.tvPetName.text = pet.name
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = pet.name
        binding.tvPetType.text = getString(R.string.detail_type, pet.type)
        binding.tvPetAge.text = getString(R.string.detail_age, pet.age)
        binding.tvPetCity.text = getString(R.string.detail_city, pet.city)
        binding.tvPetDescription.text = pet.description

        binding.btnContact.setOnClickListener {
            openWhatsApp(pet.contactPhone, pet.name)
        }
    }

    private fun openWhatsApp(phone: String?, petName: String) {
        val normalized = phone?.filter { it.isDigit() }
        if (normalized.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.whatsapp_not_found, Toast.LENGTH_SHORT).show()
            return
        }
        val message = getString(R.string.detail_pet_info) + ": " + petName
        val uri = Uri.parse("https://wa.me/$normalized?text=" + Uri.encode(message))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.whatsapp")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.whatsapp_not_found, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PET_ID = "pet_id"

        fun newInstance(id: Long): PetDetailFragment {
            return PetDetailFragment().apply {
                arguments = Bundle().apply { putLong(ARG_PET_ID, id) }
            }
        }
    }
}
