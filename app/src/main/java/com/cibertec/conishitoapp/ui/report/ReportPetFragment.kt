package com.cibertec.conishitoapp.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cibertec.conishitoapp.R
import com.cibertec.conishitoapp.databinding.FragmentReportPetBinding

class ReportPetFragment : Fragment() {

    private var _binding: FragmentReportPetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSendReport.setOnClickListener {
            Toast.makeText(requireContext(), R.string.report_sent, Toast.LENGTH_SHORT).show()
            binding.etPetName.text = null
            binding.etReportCity.text = null
            binding.etReportDescription.text = null
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.report_title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
