package ir.es.mohammad.moneytracker.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Transaction.TABLE_NAME)
data class Transaction(
    val amount: Long,
    @ColumnInfo(name = "transaction_type")
    val transactionType: TransactionType,
    val date: Long,
    @Embedded
    val category: Category,
    val note: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    val id: Long = 0L,
) {
    companion object {
        const val TABLE_NAME = "transaction_table"
    }
}