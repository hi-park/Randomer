package com.hipark.randomer.data.Source.remote

import android.os.Handler
import com.google.common.collect.Lists
import com.hipark.randomer.data.Item
import com.hipark.randomer.data.Source.ItemsDataSource

object ItemRemoteDataSource : ItemsDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 5000L

    private var ITEMS_SERVICE_DATA = LinkedHashMap<String, Item>(2)

    init {

        addItem("Hot Coffee", "StarBucks")
        addItem("Orange Juice", "Juicy")

    }

    private fun addItem(title: String, description: String) {

        val newItem = Item(title, description)
        ITEMS_SERVICE_DATA.put(newItem.id, newItem)
    }

    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {

        val items = Lists.newArrayList(ITEMS_SERVICE_DATA.values)

        Handler().postDelayed({
            callback.onItemsLoaded(items)

        }, SERVICE_LATENCY_IN_MILLIS)

    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {

        val item = ITEMS_SERVICE_DATA[itemId]

        with(Handler()) {

            if(item != null) {
                postDelayed({
                    callback.onItemLoaded(item)
                }, SERVICE_LATENCY_IN_MILLIS)
            } else {
                postDelayed({
                    callback.onDataNotAvailable()
                }, SERVICE_LATENCY_IN_MILLIS)
            }
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
}