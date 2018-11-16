package com.hipark.randomer

import android.content.Context
import com.hipark.randomer.data.FakeItemsRemoteDataSource
import com.hipark.randomer.data.Source.ItemsRepository
import com.hipark.randomer.data.Source.RandomerDatabase
import com.hipark.randomer.data.Source.local.ItemsLocalDataSource
import com.hipark.randomer.util.AppExecutors

object Injection {
    fun provideItemRepository(context: Context): ItemsRepository {
        val database = RandomerDatabase.getInstance(context)
        return ItemsRepository.getInstance(FakeItemsRemoteDataSource.getInstance(),
            ItemsLocalDataSource.getInstance(AppExecutors(), database.itemDao()))
    }
}