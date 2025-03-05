package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
    data class Product(
        @PrimaryKey(autoGenerate = true) val id: Int? = null,
        val name: String,
        val description: String,
        val price: Double,
        val imageUrl: String,
        val category: String
    )

