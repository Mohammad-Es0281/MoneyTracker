package ir.es.mohammad.moneytracker.data

import ir.es.mohammad.moneytracker.data.local.ILocalDataSource
import ir.es.mohammad.moneytracker.data.util.callDatabase
import ir.es.mohammad.moneytracker.data.util.callDatabaseFlow
import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val localDataSource: ILocalDataSource) {
    suspend fun addTransaction(transaction: Transaction): Flow<Result<Unit>> {
        return callDatabase { localDataSource.insertTransaction(transaction) }
    }

    suspend fun getTransactionsByDate(
        startDate: Long,
        endDate: Long,
    ): Flow<Result<List<Transaction>>> {
        return callDatabaseFlow { localDataSource.getTransactionsByDate(startDate, endDate) }
    }

    suspend fun getTransaction(id: Long): Flow<Result<Transaction>> {
        return callDatabase { localDataSource.getTransaction(id) }
    }

    suspend fun editTransaction(transaction: Transaction): Flow<Result<Unit>> {
        return callDatabase { localDataSource.editTransaction(transaction) }
    }

    suspend fun getCategories(): Flow<Result<List<Category>>> {
        return callDatabase { localDataSource.getAllCategories() }
    }

    suspend fun addCategory(category: Category): Flow<Result<Unit>> {
        return callDatabase { localDataSource.insertCategory(category) }
    }
}