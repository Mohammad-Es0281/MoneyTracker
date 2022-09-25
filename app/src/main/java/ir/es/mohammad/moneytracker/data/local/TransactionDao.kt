package ir.es.mohammad.moneytracker.data.local

import androidx.room.*
import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM ${Transaction.TABLE_NAME}")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM ${Transaction.TABLE_NAME} WHERE transaction_id = :id")
    suspend fun getTransaction(id: Long): Transaction

    @Update()
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM ${Transaction.TABLE_NAME} WHERE date >= :startDate AND date < :endDate")
    fun getTransactionsByDate(startDate: Long, endDate: Long): Flow<List<Transaction>>
}