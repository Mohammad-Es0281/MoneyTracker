package ir.es.mohammad.moneytracker.data

import ir.es.mohammad.moneytracker.util.Result
import ir.es.mohammad.moneytracker.data.local.ILocalDataSource
import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TransactionsRepository @Inject constructor(private val localDataSource: ILocalDataSource) {
    suspend fun addTransaction(transaction: Transaction): Flow<Result<Unit>> {
        return callDatabase { localDataSource.insertTransaction(transaction) }
    }

    suspend fun getAllTransactions(): Flow<Result<List<Transaction>>> {
        return callDatabase { localDataSource.getAllTransactions() }
    }

    private suspend fun <T : Any> callDatabase(block: suspend () -> T): Flow<Result<T>> {
        return flow<Result<T>> { emit(Result.Success(block())) }
            .onStart { emit(Result.Loading()) }
            .catch { cause -> emit(Result.Error(cause)) }
    }
}