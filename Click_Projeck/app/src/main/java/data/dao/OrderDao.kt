package data.dao

import androidx.room.*
import data.Order
import data.OrderItem

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order): Long

    @Insert
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getOrdersByUser(userId: Int): List<Order>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Int): List<OrderItem>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    suspend fun getAllOrders(): List<Order>
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Int): Order?

    @Update
    suspend fun updateOrder(order: Order)

}