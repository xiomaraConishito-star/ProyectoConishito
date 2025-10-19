package com.cibertec.conishitoapp.ui.user

import android.os.Bundle
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cibertec.conishitoapp.R
import com.cibertec.conishitoapp.data.User
import com.cibertec.conishitoapp.data.local.DatabaseRepository
import com.cibertec.conishitoapp.databinding.ScreenRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private var _binding: ScreenRegisterBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { DatabaseRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScreenRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCities()
        setupPhoneField()
        binding.tvGoLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnCreateAccount.setOnClickListener {
            handleRegister()
        }
    }

    private fun setupPhoneField() {
        binding.etPhone.filters = arrayOf(InputFilter.LengthFilter(9))
        binding.etPhone.keyListener = DigitsKeyListener.getInstance("0123456789")
    }

    private fun setupCities() {
        val cities = resources.getStringArray(R.array.default_cities)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cities)
        binding.actCity.setAdapter(adapter)
    }

    private fun handleRegister() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()
        val confirm = binding.etConfirm.text?.toString()?.trim().orEmpty()
        val phone = binding.etPhone.text?.toString()?.trim()
        val city = binding.actCity.text?.toString()?.trim()
        val termsAccepted = binding.cbTerms.isChecked

        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirm.error = null
        binding.tilPhone.error = null

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(requireContext(), R.string.fields_required, Toast.LENGTH_SHORT).show()
            if (name.isEmpty()) binding.tilName.error = getString(R.string.fields_required)
            if (email.isEmpty()) binding.tilEmail.error = getString(R.string.fields_required)
            if (password.isEmpty()) binding.tilPassword.error = getString(R.string.fields_required)
            if (confirm.isEmpty()) binding.tilConfirm.error = getString(R.string.fields_required)
            return
        }

        if (!email.contains("@")) {
            binding.tilEmail.error = getString(R.string.email_invalid)
            Toast.makeText(requireContext(), R.string.email_invalid, Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirm) {
            binding.tilConfirm.error = getString(R.string.password_mismatch)
            Toast.makeText(requireContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show()
            return
        }

        if (!phone.isNullOrEmpty()) {
            val digitsOnly = phone.all { it.isDigit() }
            if (!digitsOnly || phone.length > 9) {
                binding.tilPhone.error = getString(R.string.phone_invalid)
                Toast.makeText(requireContext(), R.string.phone_invalid, Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (!termsAccepted) {
            Toast.makeText(requireContext(), R.string.terms_required, Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val registered = withContext(Dispatchers.IO) {
                repository.registerUser(
                    User(
                        fullName = name,
                        email = email,
                        password = password,
                        phone = phone,
                        city = city
                    )
                )
            }

            if (registered) {
                Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), R.string.register_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
