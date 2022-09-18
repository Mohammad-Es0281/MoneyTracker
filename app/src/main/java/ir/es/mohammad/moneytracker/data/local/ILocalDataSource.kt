package ir.es.mohammad.moneytracker.data.local

import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun getAllTransactions(): Flow<List<Transaction>>
}