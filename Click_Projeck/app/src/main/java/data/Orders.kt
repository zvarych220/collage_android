package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val userId: Int,
    val totalAmount: Double,
    val status: String,
    val createdAt: String
)