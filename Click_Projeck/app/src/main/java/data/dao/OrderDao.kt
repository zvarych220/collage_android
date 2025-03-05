package data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import data.Order

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId")
    suspend fun getOrdersByUser(userId: Int): List<Order>

    @Insert
    suspend fun placeOrder(order: Order): Long
}
