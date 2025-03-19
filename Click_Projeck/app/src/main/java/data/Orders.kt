package data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val userId: Int,
    val totalAmount: Double,
    val status: String,
    val createdAt: String
)

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("productId")
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val price: Double
)