package ir.es.mohammad.moneytracker.data.local

import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun editTransaction(transaction: Transaction)

    suspend fun getTransaction(id: Long): Transaction

    suspend fun getTransactionsByDate(startDate: Long, endDate: Long): Flow<List<Transaction>>

    suspend fun insertCategory(category: Category)

    suspend fun getAllCategories(): List<Category>
}