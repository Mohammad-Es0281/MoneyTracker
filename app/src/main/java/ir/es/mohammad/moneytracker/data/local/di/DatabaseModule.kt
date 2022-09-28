package ir.es.mohammad.moneytracker.data.local.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.data.local.*
import ir.es.mohammad.moneytracker.data.local.dao.CategoryDao
import ir.es.mohammad.moneytracker.data.local.dao.TransactionDao
import ir.es.mohammad.moneytracker.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDataBase {
        lateinit var appDataBase: AppDataBase
        val callbackInsertDefaultCategories = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                CoroutineScope(Dispatchers.IO).launch {
                    appDataBase.categoriesDao().insertAll(getDefaultCategories(appContext))
                }
            }
        }
        appDataBase =
            Room.databaseBuilder(appContext,
                AppDataBase::class.java,
                AppDataBase::class.java.simpleName)
                .addCallback(callbackInsertDefaultCategories)
                .build()
        return appDataBase
    }

    @Provides
    @Singleton
    fun provideTransactionDao(appDataBase: AppDataBase) = appDataBase.transactionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(appDataBase: AppDataBase) = appDataBase.categoriesDao()

    @Provides
    @Singleton
    fun provideLocalDataSource(
        transactionDao: TransactionDao,
        categoryDao: CategoryDao,
    ): ILocalDataSource {
        return LocalDataSource(transactionDao, categoryDao)
    }

    private fun getDefaultCategories(appContext: Context): List<Category> {
        return listOf(
            R.string.salary,
            R.string.car,
            R.string.home,
            R.string.clothes,
            R.string.food,
            R.string.drink,
            R.string.entertainment,
            R.string.transport,
            R.string.shopping,
            R.string.phone,
            R.string.medical,
            R.string.grocery,
            R.string.gift,
            R.string.sports,
            R.string.award,
            R.string.other,
        ).map { stringId -> Category(appContext.resources.getString(stringId)) }
    }
}