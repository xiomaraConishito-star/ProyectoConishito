package com.cibertec.conishitoapp.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cibertec.conishitoapp.R
import com.cibertec.conishitoapp.data.local.DatabaseRepository
import com.cibertec.conishitoapp.data.local.SessionManager
import com.cibertec.conishitoapp.databinding.ScreenLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserFragment : Fragment() {

    private var _binding: ScreenLoginBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { DatabaseRepository(requireContext()) }
    private val sessionManager by lazy { SessionManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScreenLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener { handleLogin() }
        binding.btnLogout.setOnClickListener { handleLogout() }
        binding.tvRegister.setOnClickListener { openRegister() }

        updateSessionState()
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.user_title)
        updateSessionState()
    }

    private fun handleLogin() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), R.string.fields_required, Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { repository.loginUser(email, password) }

            if (user != null) {
                sessionManager.saveUser(user)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_success, user.fullName),
                    Toast.LENGTH_SHORT
                ).show()
                updateSessionState()
            } else {
                Toast.makeText(requireContext(), R.string.login_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLogout() {
        sessionManager.clear()
        Toast.makeText(requireContext(), R.string.logout_success, Toast.LENGTH_SHORT).show()
        updateSessionState()
    }

    private fun openRegister() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun updateSessionState() {
        val userName = sessionManager.getUserName()
        val isLoggedIn = userName != null

        binding.cardSession.isVisible = isLoggedIn
        binding.tvWelcome.text = userName?.let { getString(R.string.login_success, it) } ?: ""

        binding.tilEmail.isVisible = !isLoggedIn
        binding.tilPassword.isVisible = !isLoggedIn
        binding.tvForgot.isVisible = !isLoggedIn
        binding.btnLogin.isVisible = !isLoggedIn
        binding.llRegister.isVisible = !isLoggedIn

        if (isLoggedIn) {
            binding.tvSubtitle.text = getString(R.string.login_success, userName!!)
        } else {
            binding.tvSubtitle.text = getString(R.string.login_required)
            binding.etEmail.text = null
            binding.etPassword.text = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
