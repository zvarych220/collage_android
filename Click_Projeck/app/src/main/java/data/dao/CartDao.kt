package data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import data.CartItem

@Dao
interface CartDao {
    @Insert
    suspend fun insert(cartItem: CartItem)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    suspend fun getCartItemsByUser(userId: Int): List<CartItem>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getCartItemByUserAndProduct(userId: Int, productId: Int): CartItem?

    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun deleteCartItem(cartItemId: Int)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    suspend fun updateQuantity(cartItemId: Int, quantity: Int)
}