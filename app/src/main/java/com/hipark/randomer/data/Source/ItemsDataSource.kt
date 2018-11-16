package com.hipark.randomer.data.Source

import com.hipark.randomer.data.Item

interface ItemsDataSource {

    interface LoadItemsCallback {
        fun onItemsLoaded(items: List<Item>)
        fun onDataNotAvaiilable()
    }

    interface GetItemCallback {
        fun onItemLoaded(item: Item)
        fun onDataNotAvailable()
    }

    fun getItems(callback: ItemsDataSource.LoadItemsCallback)

    fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback)

    fun saveItem(item: Item)

    fun deleteAllItems()

    fun deleteItem(itemId: String)

    fun refreshItems()
}