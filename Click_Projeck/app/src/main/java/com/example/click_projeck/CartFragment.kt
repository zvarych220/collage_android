package com.example.click_projeck

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.click_projeck.databinding.FragmentCartBinding
import data.AppDatabase
import data.CartItem
import data.Product
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Налаштування RecyclerView
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                updateCartItemQuantity(cartItem, newQuantity)
            },
            onItemRemoved = { cartItem ->
                removeCartItem(cartItem)
            }
        )

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        // Завантаження даних з кошика
        loadCartItems()
    }

    private fun loadCartItems() {
        lifecycleScope.launch {
            try {
                val sessionManager = SessionManager(requireContext())
                val userEmail = sessionManager.getUserDetails()[SessionManager.KEY_USER_EMAIL]
                Log.d("CartFragment", "User email: $userEmail")
                if (userEmail == null) {
                    Toast.makeText(requireContext(), "Користувач не авторизований", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val userDao = AppDatabase.getInstance(requireContext()).userDao()
                val user = userDao.getUserByEmail(userEmail)
                if (user == null) {
                    Toast.makeText(requireContext(), "Користувач не знайдений", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val cartDao = AppDatabase.getInstance(requireContext()).cartDao()
                val cartItems = cartDao.getCartItemsByUser(user.id!!)

                // Отримання деталей товарів
                val productDao = AppDatabase.getInstance(requireContext()).productDao()
                val cartItemsWithDetails = cartItems.map { cartItem ->
                    val product = productDao.getProductById(cartItem.productId)
                    CartItemWithDetails(cartItem, product)
                }

                cartAdapter.submitList(cartItemsWithDetails)
            } catch (e: Exception) {

            }
        }
    }

    private fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        lifecycleScope.launch {
            try {
                val cartDao = AppDatabase.getInstance(requireContext()).cartDao()
                if (newQuantity > 0) {
                    cartDao.updateQuantity(cartItem.id, newQuantity)
                } else {
                    cartDao.deleteCartItem(cartItem.id)
                }
                loadCartItems() // Оновити список після зміни
            } catch (e: Exception) {
            }
        }
    }

    private fun removeCartItem(cartItem: CartItem) {
        lifecycleScope.launch {
            try {
                val cartDao = AppDatabase.getInstance(requireContext()).cartDao()
                cartDao.deleteCartItem(cartItem.id)
                loadCartItems() // Оновити список після видалення
            } catch (e: Exception) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class CartItemWithDetails(
        val cartItem: CartItem,
        val product: Product?
    )
}