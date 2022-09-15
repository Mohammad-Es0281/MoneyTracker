package ir.es.mohammad.moneytracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.es.mohammad.moneytracker.model.Transaction

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM ${Transaction.TABLE_NAME}")
    suspend fun getAllTransactions(): List<Transaction>
}