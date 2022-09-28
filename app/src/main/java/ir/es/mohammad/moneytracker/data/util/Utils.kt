package ir.es.mohammad.moneytracker.data.util

import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.flow.*

suspend fun <T : Any> callDatabase(block: suspend () -> T): Flow<Result<T>> {
    return flow<Result<T>> { emit(Result.Success(block())) }
        .onStart { emit(Result.Loading()) }
        .retry(1)
        .catch { cause -> emit(Result.Error(cause)) }
}

suspend fun <T : Any> callDatabaseFlow(block: suspend () -> Flow<T>): Flow<Result<T>> {
    return flow<Result<T>> { block().collect { value -> emit(Result.Success(value)) } }
        .onStart { emit(Result.Loading()) }
        .retry(1)
        .catch { cause -> emit(Result.Error(cause)) }
}