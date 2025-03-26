package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.click_projeck.databinding.FragmentMainBinding
import data.AppDatabase
import data.Order
import data.dao.OrderDao
import data.dao.UserDao
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderDao: OrderDao
    private lateinit var userDao: UserDao
    private lateinit var sessionManager: SessionManager
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ініціалізація бази даних та сесії
        database = AppDatabase.getInstance(requireContext())
        orderDao = database.orderDao()
        userDao = database.userDao()
        sessionManager = SessionManager(requireContext())

        // Налаштування адаптера
        setupAdapter()

        // Завантаження замовлень
        loadOrders()
    }

    private fun setupAdapter() {
        orderAdapter = OrderAdapter { order ->
            order.id?.let { id ->
                findNavController().navigate(
                    R.id.action_mainFragment_to_orderUserDetailFragment,
                    bundleOf("order_id" to order.id) // Make sure 'order' is available in your scope
                )
            }
        }

        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            try {
                val userEmail = sessionManager.getUserDetails()[SessionManager.KEY_USER_EMAIL]
                if (userEmail != null) {
                    val user = userDao.getUserByEmail(userEmail)
                    user?.let {
                        val orders = orderDao.getOrdersByUser(it.id ?: 0)
                        if (orders.isEmpty()) {
                            showEmptyState()
                        } else {
                            showOrders(orders)
                        }
                    } ?: showEmptyState()
                } else {
                    showEmptyState()
                }
            } catch (e: Exception) {
                showErrorState()
            }
        }
    }

    private fun showOrders(orders: List<Order>) {
        binding.apply {
            ordersRecyclerView.visibility = View.VISIBLE
            emptyStateView.visibility = View.GONE
            errorStateView.visibility = View.GONE
        }
        orderAdapter.submitList(orders)
    }

    private fun showEmptyState() {
        binding.apply {
            ordersRecyclerView.visibility = View.GONE
            emptyStateView.visibility = View.VISIBLE
            errorStateView.visibility = View.GONE
        }
    }

    private fun showErrorState() {
        binding.apply {
            ordersRecyclerView.visibility = View.GONE
            emptyStateView.visibility = View.GONE
            errorStateView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}