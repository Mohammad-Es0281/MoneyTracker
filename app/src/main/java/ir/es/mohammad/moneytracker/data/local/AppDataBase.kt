package ir.es.mohammad.moneytracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction

@Database(entities = [Transaction::class, Category::class], exportSchema = false, version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoriesDao(): CategoryDao
}