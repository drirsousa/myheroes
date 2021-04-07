package br.com.rosait.myheroes.common.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import br.com.rosait.myheroes.common.entity.ItemEntity

@Dao
interface ItemDao {

    @Query("SELECT * FROM item ORDER BY id ASC")
    fun getAllItems() : LiveData<List<ItemEntity>>

    @Query("SELECT * FROM item ORDER BY id ASC")
    fun getAllItemsList() : List<ItemEntity>

    @Query("SELECT * FROM item WHERE item.id = :myId")
    fun getItem(myId: Long) : ItemEntity

    @Insert
    fun insertItem(item: ItemEntity) : Long

    @Insert(onConflict = IGNORE)
    fun insertOrReplaceItems(vararg item: ItemEntity)

    @Delete
    fun deleteItem(item: ItemEntity)

    @Query("DELETE FROM item")
    fun deleteAllItems()

    @Update
    fun updateItem(item: ItemEntity)

}