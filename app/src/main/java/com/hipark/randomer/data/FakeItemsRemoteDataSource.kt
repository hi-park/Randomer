package com.hipark.randomer.data

import com.google.common.collect.Lists
import com.hipark.randomer.data.Source.ItemsDataSource

class FakeItemsRemoteDataSource private constructor() : ItemsDataSource {

    private val ITEMS_SERVICE_DATA = LinkedHashMap<String, Item>()

    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {
        callback.onItemsLoaded(Lists.newArrayList(ITEMS_SERVICE_DATA.values))
    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {

        val item = ITEMS_SERVICE_DATA[itemId]

        if(item != null) {
            callback.onItemLoaded(item)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun saveItem(item: Item) {
        ITEMS_SERVICE_DATA.put(item.id, item)
    }

    override fun deleteAllItems() {
        ITEMS_SERVICE_DATA.clear()
    }

    override fun deleteItem(itemId: String) {
        ITEMS_SERVICE_DATA.remove(itemId)
    }

    override fun refreshItems() {

    }

    companion object {

        private lateinit var INSTANCE: FakeItemsRemoteDataSource
        private var needsNewInstance = true

        @JvmStatic fun getInstance() : FakeItemsRemoteDataSource {
            if(needsNewInstance) {
                INSTANCE = FakeItemsRemoteDataSource()
                needsNewInstance = false
            }
            return INSTANCE
        }
    }

}