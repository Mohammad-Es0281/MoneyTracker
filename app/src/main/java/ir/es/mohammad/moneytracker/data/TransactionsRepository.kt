package ir.es.mohammad.moneytracker.data

import ir.es.mohammad.moneytracker.data.local.ILocalDataSource
import ir.es.mohammad.moneytracker.data.util.callDatabase
import ir.es.mohammad.moneytracker.data.util.callDatabaseFlow
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class TransactionsRepository @Inject constructor(private val localDataSource: ILocalDataSource) {
    suspend fun addTransaction(transaction: Transaction): Flow<Result<Unit>> {
        return callDatabase { localDataSource.insertTransaction(transaction) }
    }

    suspend fun getAllTransactions(): Flow<Result<List<Transaction>>> {
        return callDatabaseFlow { localDataSource.getAllTransactions() }
    }

    suspend fun getTransactionsByDate(startDate: Long, endDate: Long): Flow<Result<List<Transaction>>> {
        return callDatabaseFlow { localDataSource.getTransactionsByDate(startDate, endDate) }
    }
}