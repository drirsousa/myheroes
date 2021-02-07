package br.com.rosait.marvelcharacters.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.rosait.marvelcharacters.common.di.App
import br.com.rosait.marvelcharacters.common.di.AppModule
import br.com.rosait.marvelcharacters.common.di.DaggerAppComponent
import br.com.rosait.marvelcharacters.common.model.ItemCharacters
import br.com.rosait.marvelcharacters.common.model.ResponseResult
import br.com.rosait.marvelcharacters.common.entity.ItemEntity
import br.com.rosait.marvelcharacters.service.ItemService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ItemViewModel(context: Context) : ViewModel() {

    private val component by lazy { DaggerAppComponent.builder().appModule(
        AppModule(
            App(),
            context
        )
    ).build() }

    private val mItemListMutable: MutableLiveData<List<ItemCharacters>> = MutableLiveData()
    val mItemList: LiveData<List<ItemCharacters>> = mItemListMutable

    private val mItemMutable: MutableLiveData<ItemCharacters> = MutableLiveData()
    val mItem: LiveData<ItemCharacters> = mItemMutable

    private val mItemFavoriteMutable: MutableLiveData<ItemEntity> = MutableLiveData()
    val mItemFavorite: LiveData<ItemEntity> = mItemFavoriteMutable

    private val mLoadingMutable: MutableLiveData<Boolean> = MutableLiveData()
    val mLoading: LiveData<Boolean> = mLoadingMutable

    private val mErrorMessageMutable: MutableLiveData<String> = MutableLiveData()
    val mErrorMessage: LiveData<String> = mErrorMessageMutable

    @Inject
    lateinit var mItemService: ItemService

    init {
        component.inject(this)
    }

    fun getItems() {
        mItemService.getItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { setLoading(true) }
            .doAfterTerminate { setLoading(false) }
            .subscribe({
                setItemList(it)
            }, { throwable ->
                Throwable(throwable)
                logError(throwable.message)
            })
    }

    fun getDetailItem(id: Long) {
        mItemService.getDetailItem(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { setLoading(true) }
            .doAfterTerminate { setLoading(false) }
            .subscribe({
                setItem(it)
            }, { throwable ->
                Throwable(throwable)
                logError(throwable.message)
            })
    }

    fun getFavorites() : LiveData<List<ItemEntity>> {
        setLoading(true)
        return mItemService.getFavorites()
    }

    fun getDetailFavorite(id: Long) {
        setItemFavorite(mItemService.getDetailFavorite(id))
    }

    fun saveFavorite(item: Any) {
        val itemEntity = getItemEntity(item)
        itemEntity?.let { mItemService.saveFavorite(it) }
    }

    fun deleteFavorite(item: Any) {
        val itemEntity = getItemEntity(item)
        itemEntity?.let { mItemService.deleteFavorite(it) }
    }

    private fun getItemEntity(item: Any): ItemEntity? {
        var entity: ItemEntity? = null
        when (item) {
            is ItemCharacters -> {
                entity = changeToItemEntity(item)
            }
            is ItemEntity -> entity = item
        }
        return entity
    }

    fun setItemList(responseResult: ResponseResult) {
        responseResult.let { result ->
            val items = result.data.results
            if(items.isNotEmpty())
                mItemListMutable.value = items
        }
    }

    private fun setItem(responseResult: ResponseResult) {
        responseResult.let { result ->
            val items = result.data.results
            if(items.isNotEmpty())
                mItemMutable.value = items.firstOrNull()
        }
    }

    fun setItemFavorite(itemFavorite: ItemEntity) {
        mItemFavoriteMutable.value = itemFavorite
    }

    fun setLoading(value: Boolean) {
        mLoadingMutable.value = value
    }

    fun logError(message: String?) {
        mErrorMessageMutable.value = message
    }

    private fun changeToItemEntity(item: ItemCharacters) : ItemEntity {
        return item.let {
            ItemEntity(it.id, it.name, it.description ?: "", "${it.thumbnail.path}.${it.thumbnail.extension}")
        }
    }
}