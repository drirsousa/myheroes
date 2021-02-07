package br.com.rosait.marvelcharacters.common.repository

import android.content.Context
import br.com.rosait.marvelcharacters.common.model.ResponseResult
import io.reactivex.Single

class ApiRepository(context: Context) {

    private val endpoint = RestManager.getEndpoint(context)

    fun getItems(ts: Long, apiKey: String, hash: String): Single<ResponseResult> = endpoint.getItems(ts, apiKey, hash)

    fun getItemById(id: Long, ts: Long, apiKey: String, hash: String): Single<ResponseResult> = endpoint.getItemById(id, ts, apiKey, hash)
}