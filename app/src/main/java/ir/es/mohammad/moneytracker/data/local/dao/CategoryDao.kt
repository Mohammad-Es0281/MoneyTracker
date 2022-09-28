package ir.es.mohammad.moneytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.es.mohammad.moneytracker.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert()
    suspend fun insert(category: Category)

    @Insert()
    suspend fun insertAll(category: List<Category>)

    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    suspend fun getAll(): List<Category>
}