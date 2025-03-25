package com.example.click_projeck

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.click_projeck.databinding.FragmentOrderDetailBinding
import data.AppDatabase
import data.Order
import data.OrderItem
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {
    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderItemAdapter: OrderItemAdapter
    private var currentStatus: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderItemAdapter = OrderItemAdapter()
        binding.orderItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.orderItemsRecyclerView.adapter = orderItemAdapter

        val orderId = arguments?.getInt("order_id") ?: -1
        if (orderId != -1) {
            loadOrderDetails(orderId)
        }

        binding.updateStatusButton.setOnClickListener {
            updateOrderStatus()
        }
    }

    private fun loadOrderDetails(orderId: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val order = db.orderDao().getOrderById(orderId)
            val orderItems = db.orderDao().getOrderItems(orderId)
            val user = order?.userId?.let { db.userDao().getUserById(it) }

            order?.let {
                currentStatus = it.status
                displayOrderInfo(it, user)
                setupStatusSpinner(it.status)
                orderItemAdapter.submitList(orderItems)
            }
        }
    }

    private fun displayOrderInfo(order: Order, user: data.User?) {
        binding.orderId.text = "Order #${order.id}"
        binding.orderStatus.text = "Status: ${order.status}"
        binding.orderTotal.text = "Total: ${order.totalAmount}"
        binding.orderDate.text = "Date: ${order.createdAt}"

        binding.customerName.text = "Name: ${order.recipientName}"
        binding.customerPhone.text = "Phone: ${order.phoneNumber}"
        binding.deliveryAddress.text = "Address: ${order.deliveryAddress}"
        binding.paymentMethod.text = "Payment: ${order.paymentMethod}"
    }

    private fun setupStatusSpinner(currentStatus: String) {
        val statusFlow = listOf(
            "Обробка",
            "Замовлення прийняте",
            "Відправлено",
            "В дорозі",
            "Отримано",
            "Скасовано"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            statusFlow.filter { canTransitionTo(currentStatus, it) }
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.statusSpinner.adapter = adapter

        binding.statusSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = parent?.getItemAtPosition(position).toString()
                binding.trackingNumberLayout.visibility = if (selectedStatus == "Відправлено") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        if (currentStatus == "Отримано" || currentStatus == "Скасовано") {
            binding.statusUpdateCard.visibility = View.GONE
        }
    }

    private fun canTransitionTo(currentStatus: String, newStatus: String): Boolean {
        if (newStatus == "Скасовано") return true

        return when (currentStatus) {
            "Обробка" -> newStatus == "Замовлення прийняте"
            "Замовлення прийняте" -> newStatus == "Відправлено"
            "Відправлено" -> newStatus == "В дорозі"
            "В дорозі" -> newStatus == "Отримано"
            else -> false
        }
    }

    private fun updateOrderStatus() {
        val newStatus = binding.statusSpinner.selectedItem as String
        val trackingNumber = binding.trackingNumberInput.text.toString()

        if (newStatus == "Відправлено" && trackingNumber.isBlank()) {
            Toast.makeText(requireContext(), "Please enter tracking number", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val orderId = arguments?.getInt("order_id") ?: -1
            val order = db.orderDao().getOrderById(orderId)

            order?.let {
                val updatedOrder = it.copy(status = newStatus)
                db.orderDao().updateOrder(updatedOrder)

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Status updated", Toast.LENGTH_SHORT).show()
                    loadOrderDetails(orderId)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}