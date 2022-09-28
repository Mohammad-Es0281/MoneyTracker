package ir.es.mohammad.moneytracker.data.local

import ir.es.mohammad.moneytracker.data.local.dao.CategoryDao
import ir.es.mohammad.moneytracker.data.local.dao.TransactionDao
import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
) : ILocalDataSource {
    override suspend fun insertTransaction(transaction: Transaction) {
        return transactionDao.insert(transaction)
    }

    override suspend fun editTransaction(transaction: Transaction) {
        return transactionDao.update(transaction)
    }
    override suspend fun getTransaction(id: Long): Transaction {
        return transactionDao.get(id)
    }

    override suspend fun getTransactionsByDate(
        startDate: Long,
        endDate: Long,
    ): Flow<List<Transaction>> {
        return transactionDao.getByDate(startDate, endDate)
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAll()
    }

    override suspend fun insertCategory(category: Category) {
        return categoryDao.insert(category)
    }
}