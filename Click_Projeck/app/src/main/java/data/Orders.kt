package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val userId: Int, // ID користувача, який зробив замовлення
    val totalAmount: Double, // Загальна сума замовлення
    val status: String = "Обробка", // Статус замовлення (по дефолту "Обробка")
    val createdAt: String // Дата створення замовлення
)