package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val userId: Int,
    val totalAmount: Double,
    val status: String = "Обробка",
    val createdAt: String,
    val recipientName: String,
    val phoneNumber: String,
    val deliveryAddress: String,
    val paymentMethod: String
)