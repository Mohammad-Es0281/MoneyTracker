package ir.es.mohammad.moneytracker.data.local

import ir.es.mohammad.moneytracker.model.Transaction

interface ILocalDataSource {
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun getAllTransactions(): List<Transaction>
}