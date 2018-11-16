package com.hipark.randomer.data.Source

import android.arch.persistence.room.*
import com.hipark.randomer.data.Item

@Dao
interface ItemsDao {

    @Query("SELECT * FROM Items") fun getItems() : List<Item>

    @Query("SELECT * FROM Items WHERE entryid = :itemId") fun getItemById(itemId: String) : Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertItem(item: Item)

    @Update fun updateItem(item: Item) : Int

    @Query("DELETE FROM Items WHERE entryid = :itemId") fun deleteItemById(itemId: String): Int

    @Query("DELETE FROM Items") fun deleteItems()

}