package ir.es.mohammad.moneytracker.data.local

import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun getAllTransactions(): Flow<List<Transaction>>

    suspend fun getTransactionsByDate(startDate: Long, endDate: Long): Flow<List<Transaction>>

    suspend fun getAllCategories(): Flow<List<Category>>

    suspend fun insertCategory(category: Category)
}