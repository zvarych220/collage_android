package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.click_projeck.databinding.FragmentOrderManagementBinding
import data.AppDatabase
import data.Order
import kotlinx.coroutines.launch

class OrderManagementFragment : Fragment() {
    private var _binding: FragmentOrderManagementBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrderAdapter { order ->
            val bundle = Bundle().apply {
                putInt("order_id", order.id ?: -1)
            }
            findNavController().navigate(
                R.id.action_orderManagementFragment_to_orderDetailFragment,
                bundle
            )
        }

        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ordersRecyclerView.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val orders = db.orderDao().getAllOrders()
            adapter.submitList(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}