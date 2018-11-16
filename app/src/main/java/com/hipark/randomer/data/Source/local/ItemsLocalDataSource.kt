package com.hipark.randomer.data.Source.local

import com.hipark.randomer.data.Item
import com.hipark.randomer.data.Source.ItemsDao
import com.hipark.randomer.data.Source.ItemsDataSource
import com.hipark.randomer.util.AppExecutors

class ItemsLocalDataSource private constructor(
    val appExecutors : AppExecutors,
    val itemsDao: ItemsDao
) : ItemsDataSource {

    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {

        appExecutors.diskIO.execute {
            val items = itemsDao.getItems()
            appExecutors.mainThread.execute {

                if(items.isEmpty()) {
                    callback.onDataNotAvaiilable()
                } else {
                    callback.onItemsLoaded(items)
                }
            }
        }
    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {

        appExecutors.diskIO.execute {
            val item = itemsDao.getItemById(itemId)
            appExecutors.mainThread.execute {
                if(item != null) {
                    callback.onItemLoaded(item)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun saveItem(item: Item) {
        appExecutors.diskIO.execute{itemsDao.insertItem(item)}
    }

    override fun deleteAllItems() {
        appExecutors.diskIO.execute{itemsDao.deleteItems()}
    }

    override fun deleteItem(itemId: String) {
        appExecutors.diskIO.execute { itemsDao.deleteItemById(itemId) }
    }

    override fun refreshItems() {

    }

    companion object {

        private var INSTANCE: ItemsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, itemsDao: ItemsDao) : ItemsLocalDataSource {

            if(INSTANCE == null) {
                synchronized(ItemsLocalDataSource::javaClass) {
                    INSTANCE =
                            ItemsLocalDataSource(appExecutors, itemsDao)
                }
            }
            return INSTANCE!!
        }
    }
}