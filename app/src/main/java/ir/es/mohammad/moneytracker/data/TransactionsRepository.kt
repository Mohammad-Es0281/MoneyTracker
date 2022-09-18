package ir.es.mohammad.moneytracker.data

import ir.es.mohammad.moneytracker.data.local.ILocalDataSource
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

    private suspend fun <T : Any> callDatabase(block: suspend () -> T): Flow<Result<T>> {
        return flow<Result<T>> { emit(Result.Success(block())) }
            .onStart { emit(Result.Loading()) }
            .catch { cause -> emit(Result.Error(cause)) }
    }

    private suspend fun <T : Any> callDatabaseFlow(block: suspend () -> Flow<T>): Flow<Result<T>> {
        return flow<Result<T>> { block().collect { value -> emit(Result.Success(value)) } }
            .onStart { emit(Result.Loading()) }
            .catch { cause -> emit(Result.Error(cause)) }
    }
}