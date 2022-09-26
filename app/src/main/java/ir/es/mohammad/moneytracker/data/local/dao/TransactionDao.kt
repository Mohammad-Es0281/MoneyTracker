package ir.es.mohammad.moneytracker.data.local.dao

import androidx.room.*
import ir.es.mohammad.moneytracker.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert()
    suspend fun insert(transaction: Transaction)

    @Update()
    suspend fun update(transaction: Transaction)

    @Query("SELECT * FROM ${Transaction.TABLE_NAME} WHERE transaction_id = :id")
    suspend fun get(id: Long): Transaction

    @Query("SELECT * FROM ${Transaction.TABLE_NAME} WHERE date >= :startDate AND date < :endDate")
    fun getByDate(startDate: Long, endDate: Long): Flow<List<Transaction>>
}