package ir.es.mohammad.moneytracker.data.local

import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val transactionDao: TransactionDao,
) : ILocalDataSource {
    override suspend fun insertTransaction(transaction: Transaction) {
        return transactionDao.insertTransaction(transaction)
    }

    override suspend fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun getTransactionsByDate(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDate(startDate, endDate)
    }
}