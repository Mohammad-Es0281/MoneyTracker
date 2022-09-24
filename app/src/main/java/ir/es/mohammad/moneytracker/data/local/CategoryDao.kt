package ir.es.mohammad.moneytracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.es.mohammad.moneytracker.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(category: List<Category>)

    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    fun getAllCategories(): Flow<List<Category>>
}