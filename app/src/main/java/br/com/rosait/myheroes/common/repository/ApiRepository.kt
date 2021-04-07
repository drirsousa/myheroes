package br.com.rosait.myheroes.common.repository

import android.content.Context
import br.com.rosait.myheroes.common.model.ResponseResult
import io.reactivex.Single

class ApiRepository(context: Context) {

    private val endpoint = RestManager.getEndpoint(context)

    fun getItems(ts: Long, apiKey: String, hash: String, limit: Int, offset: Int): Single<ResponseResult> = endpoint.getItems(ts, apiKey, hash, limit, offset)

    fun getItemById(id: Long, ts: Long, apiKey: String, hash: String): Single<ResponseResult> = endpoint.getItemById(id, ts, apiKey, hash)
}