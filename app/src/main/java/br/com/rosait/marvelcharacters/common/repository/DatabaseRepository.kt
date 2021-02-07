package br.com.rosait.marvelcharacters.common.repository

import android.content.Context
import androidx.lifecycle.LiveData
import br.com.rosait.marvelcharacters.common.di.App
import br.com.rosait.marvelcharacters.common.di.AppModule
import br.com.rosait.marvelcharacters.common.di.DaggerAppComponent
import br.com.rosait.marvelcharacters.common.database.AppDatabase
import br.com.rosait.marvelcharacters.common.entity.ItemEntity
import javax.inject.Inject

class DatabaseRepository(context: Context) {

    private val component by lazy { DaggerAppComponent.builder().appModule(
        AppModule(
            App(),
            context
        )
    ).build() }

    @Inject
    lateinit var _db: AppDatabase

    init {
        component.inject(this)
    }

    fun getItems(): LiveData<List<ItemEntity>> {
        return _db.itemDao().getAllItems()
    }

    fun getItemById(id: Long): ItemEntity {
        return _db.itemDao().getItem(id)
    }

    fun saveFavorite(item: ItemEntity) {
        _db.itemDao().insertOrReplaceItems(item)
    }

    fun deleteFavorite(item: ItemEntity) {
        _db.itemDao().deleteItem(item)
    }
}