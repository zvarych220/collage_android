package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.click_projeck.databinding.FragmentOrderDetailBinding
import com.example.click_projeck.databinding.FragmentOrderUserDetailBinding
import data.AppDatabase
import data.Order
import data.dao.OrderDao
import kotlinx.coroutines.launch

class OrderUserDetailFragment : Fragment() {
    private var _binding: FragmentOrderUserDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderDao: OrderDao
    private var orderId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderId = arguments?.getInt("order_id") ?: 0
        if (orderId == 0) {
            showErrorAndExit()
            return
        }

        setupDatabase()
        loadOrderDetails()
    }

    private fun setupDatabase() {
        val database = AppDatabase.getInstance(requireContext())
        orderDao = database.orderDao()
    }

    private fun loadOrderDetails() {
        lifecycleScope.launch {
            val order = orderDao.getOrderById(orderId)
            order?.let { displayOrderDetails(it) } ?: showErrorAndExit()
        }
    }

    private fun displayOrderDetails(order: Order) {
        with(binding) {
            orderNumber.text = getString(R.string.order_number, order.id)
            statusValue.apply {
                text = order.status
                setTextColor(getStatusColor(order.status))
            }
            totalValue.text = getString(R.string.price_format, order.totalAmount)
            dateValue.text = order.createdAt
            customerNameValue.text = order.recipientName
            phoneValue.text = order.phoneNumber
            addressValue.text = order.deliveryAddress
            paymentValue.text = order.paymentMethod
        }
    }

    private fun getStatusColor(status: String): Int {
        return when (status.toLowerCase()) {
            "доставлено" -> ContextCompat.getColor(requireContext(), R.color.green)
            "скасовано" -> ContextCompat.getColor(requireContext(), R.color.red)
            else -> ContextCompat.getColor(requireContext(), R.color.primary_color)
        }
    }

    private fun showErrorAndExit() {
        Toast.makeText(requireContext(), R.string.order_not_found, Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}