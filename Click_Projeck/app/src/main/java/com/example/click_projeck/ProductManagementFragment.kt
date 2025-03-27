package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentProductManagementBinding

class ProductManagementFragment : Fragment() {
    private var _binding: FragmentProductManagementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnViewProducts.setOnClickListener {
            findNavController().navigate(R.id.action_productManagementFragment_to_productListFragment)
        }

        binding.btnCreateProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productManagementFragment_to_createProductFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}