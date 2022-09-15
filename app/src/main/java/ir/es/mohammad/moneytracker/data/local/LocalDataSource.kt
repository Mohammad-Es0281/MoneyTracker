package ir.es.mohammad.moneytracker.data.local

import ir.es.mohammad.moneytracker.model.Transaction
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val transactionDao: TransactionDao
) : ILocalDataSource {
    override suspend fun insertTransaction(transaction: Transaction) {
        return transactionDao.insertTransaction(transaction)
    }

    override suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }
}