package com.hipark.randomer.data.Source

import com.hipark.randomer.data.Item

class ItemsRepository(
    val itemsRemoteDataSource: ItemsDataSource,
    val itemsLocalDataSource: ItemsDataSource
) : ItemsDataSource  {

    var cachedItems: LinkedHashMap<String, Item> = LinkedHashMap()

    var cacheIsDirty = false

    override fun getItems(callback: ItemsDataSource.LoadItemsCallback) {

        if(cachedItems.isNotEmpty() && !cacheIsDirty) {
            callback.onItemsLoaded(ArrayList(cachedItems.values))
            return
        }

        if(cacheIsDirty) {
            getItemsFromRemoteDataSource(callback)
        } else {

            itemsLocalDataSource.getItems(object: ItemsDataSource.LoadItemsCallback {
                override fun onItemsLoaded(items: List<Item>) {
                    refreshCache(items)
                    callback.onItemsLoaded(ArrayList(cachedItems.values))
                }

                override fun onDataNotAvaiilable() {
                    getItemsFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun saveItem(item: Item) {

        cacheAndPerform(item) {
            itemsRemoteDataSource.saveItem(it)
            itemsLocalDataSource.saveItem(it)
        }

    }

    override fun getItem(itemId: String, callback: ItemsDataSource.GetItemCallback) {

        val itemInCache = getItemWithId(itemId)

        if(itemInCache != null) {
            callback.onItemLoaded(itemInCache)
            return
        }

        itemsLocalDataSource.getItem(itemId, object: ItemsDataSource.GetItemCallback {
            override fun onItemLoaded(item: Item) {
                cacheAndPerform(item) {
                    callback.onItemLoaded(it)
                }
            }

            override fun onDataNotAvailable() {

                itemsRemoteDataSource.getItem(itemId, object : ItemsDataSource.GetItemCallback {
                    override fun onItemLoaded(item: Item) {

                        cacheAndPerform(item) {
                            callback.onItemLoaded(it)
                        }
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }

                })
            }
        })
    }

    override fun refreshItems() {
        cacheIsDirty = true
    }

    override fun deleteAllItems() {
        itemsRemoteDataSource.deleteAllItems()
        itemsLocalDataSource.deleteAllItems()
        cachedItems.clear()
    }

    override fun deleteItem(itemId: String) {
        itemsRemoteDataSource.deleteItem(itemId)
        itemsLocalDataSource.deleteItem(itemId)
        cachedItems.remove(itemId)
    }

    private fun getItemsFromRemoteDataSource(callback: ItemsDataSource.LoadItemsCallback) {
        itemsRemoteDataSource.getItems(object: ItemsDataSource.LoadItemsCallback {
            override fun onItemsLoaded(items: List<Item>) {
                refreshCache(items)
                refreshLocalDataSource(items)
                callback.onItemsLoaded(ArrayList(cachedItems.values))
            }

            override fun onDataNotAvaiilable() {
                callback.onDataNotAvaiilable()
            }
        })
    }

    private fun refreshCache(items: List<Item>) {
        cachedItems.clear()
        items.forEach {
            cacheAndPerform(it){}
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(items: List<Item>) {
        itemsLocalDataSource.deleteAllItems()
        for(item in items) {
            itemsLocalDataSource.saveItem(item)
        }
    }

    private fun getItemWithId(id:String) = cachedItems[id]

    private inline fun cacheAndPerform(item: Item, perform: (Item) -> Unit) {
        val cachedItem = Item(item.title, item.description, item.id)
        cachedItems.put(cachedItem.id, cachedItem)
        perform(cachedItem)
    }

    companion object {

        private var INSTANCE: ItemsRepository? = null

        @JvmStatic fun getInstance(itemsRemoteDataSource: ItemsDataSource,
                                   itemsLocalDataSource: ItemsDataSource) : ItemsRepository {

            return INSTANCE ?: ItemsRepository(itemsRemoteDataSource, itemsLocalDataSource).apply {
                INSTANCE = this
            }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}