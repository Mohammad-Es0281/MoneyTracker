package ir.es.mohammad.moneytracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Transaction.TABLE_NAME)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "transaction_type")
    val transactionType: TransactionType
) {
    companion object {
        const val TABLE_NAME = "transaction_table"
    }
}