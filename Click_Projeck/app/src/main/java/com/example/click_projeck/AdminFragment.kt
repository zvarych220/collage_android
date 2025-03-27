package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnManageProducts.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_productManagementFragment)
        }

        binding.btnManageUsers.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_userListFragment)
        }

        binding.btnSystemSettings.setOnClickListener {
        }
        // Add this to onViewCreated in AdminFragment
        binding.btnManageOrders.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_orderManagementFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}