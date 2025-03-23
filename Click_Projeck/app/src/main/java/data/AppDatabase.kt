package data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import data.dao.CartDao
import data.dao.OrderDao
import data.dao.ProductDao
import data.dao.UserDao

@Database(entities = [User::class, Product::class, Order::class,CartItem::class, OrderItem::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun cartDao(): CartDao // Додаємо CartDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6,MIGRATION_6_7, MIGRATION_7_8,MIGRATION_8_9,MIGRATION_9_10)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Міграція з версії 1 до 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    DELETE FROM users 
                    WHERE id NOT IN (
                        SELECT MIN(id) 
                        FROM users 
                        GROUP BY email
                    )
                """)
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_email ON users(email)")
            }
        }

        // Міграція з версії 2 до 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        price REAL NOT NULL,
                        imageUrl TEXT NOT NULL,
                        category TEXT NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId INTEGER NOT NULL,
                        totalAmount REAL NOT NULL,
                        status TEXT NOT NULL,
                        createdAt TEXT NOT NULL
                    )
                """)
            }
        }

        // Міграція з версії 3 до 4
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN profileImage TEXT")
            }
        }

        // Міграція з версії 4 до 5
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN isAdmin INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Міграція з версії 5 до 6 (додано поле allImages для зберігання всіх фото товару)
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Додаємо нове поле allImages з обмеженням NOT NULL
                database.execSQL("ALTER TABLE products ADD COLUMN allImages TEXT NOT NULL DEFAULT '[]'")
            }
        }
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cart_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId INTEGER NOT NULL,
                        productId INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        price REAL NOT NULL,
                        FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_cart_items_userId ON cart_items(userId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_cart_items_productId ON cart_items(productId)")
            }
        }
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS cart_items")
            }
        }
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS cart_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE
            )
        """)
            }
        }
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS order_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        orderId INTEGER NOT NULL,
                        productId INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        pricePerItem REAL NOT NULL,
                        FOREIGN KEY (orderId) REFERENCES orders(id) ON DELETE CASCADE,
                        FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE
                    )
                """)
            }
        }
    }
}