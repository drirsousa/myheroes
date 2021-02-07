package br.com.rosait.marvelcharacters.service

import android.content.Context
import androidx.lifecycle.LiveData
import br.com.rosait.marvelcharacters.BuildConfig
import br.com.rosait.marvelcharacters.common.di.App
import br.com.rosait.marvelcharacters.common.di.AppModule
import br.com.rosait.marvelcharacters.common.di.DaggerAppComponent
import br.com.rosait.marvelcharacters.common.extension.toHexString
import br.com.rosait.marvelcharacters.common.model.ResponseResult
import br.com.rosait.marvelcharacters.common.repository.ApiRepository
import br.com.rosait.marvelcharacters.common.repository.DatabaseRepository
import br.com.rosait.marvelcharacters.common.entity.ItemEntity
import io.reactivex.Single
import java.security.MessageDigest
import javax.inject.Inject

class ItemService(context: Context) {

    private val component by lazy { DaggerAppComponent.builder().appModule(
        AppModule(
            App(),
            context
        )
    ).build() }

    @Inject
    lateinit var mApiRepository: ApiRepository
    @Inject
    lateinit var mDatabaseRepository: DatabaseRepository

    init {
        component.inject(this)
    }

    fun getItems() : Single<ResponseResult> {
        val ts = System.currentTimeMillis()
        val hash = createHash(ts)
        return mApiRepository.getItems(ts, BuildConfig.PUBLICKEY, hash)
    }

    fun getDetailItem(id: Long) : Single<ResponseResult> {
        val ts = System.currentTimeMillis()
        val hash = createHash(ts)
        return mApiRepository.getItemById(id, ts, BuildConfig.PUBLICKEY, hash)
    }

    fun getFavorites() : LiveData<List<ItemEntity>> = mDatabaseRepository.getItems()

    fun getDetailFavorite(id: Long) : ItemEntity = mDatabaseRepository.getItemById(id)

    fun saveFavorite(item: ItemEntity) = mDatabaseRepository.saveFavorite(item)

    fun deleteFavorite(item: ItemEntity) = mDatabaseRepository.deleteFavorite(item)

    private fun createHash(ts: Long) : String {
        val md = MessageDigest.getInstance("MD5")
        val hash = md.digest("$ts${BuildConfig.PRIVATEKEY}${BuildConfig.PUBLICKEY}".toByteArray())
        return hash.toHexString()
    }
}