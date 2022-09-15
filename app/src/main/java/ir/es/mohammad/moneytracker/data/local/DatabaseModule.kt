package ir.es.mohammad.moneytracker.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            AppDataBase::class.java,
            AppDataBase::class.java.simpleName
        ).build()

    @Provides
    @Singleton
    fun provideTransactionDao(appDataBase: AppDataBase) = appDataBase.transactionDao()

    @Provides
    @Singleton
    fun provideLocalDataSource(transactionDao: TransactionDao): ILocalDataSource {
        return LocalDataSource(transactionDao)
    }
}