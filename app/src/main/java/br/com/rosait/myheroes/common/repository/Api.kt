package br.com.rosait.myheroes.common.repository

import br.com.rosait.myheroes.common.model.ResponseResult
import io.reactivex.Single
import retrofit2.http.*

interface Api {

    @GET("characters")
    fun getItems(
        @Query("ts") ts: Long,
        @Query("apikey") apikey: String,
        @Query("hash") hash: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Single<ResponseResult>

    @GET("characters/{characterId}")
    fun getItemById(
        @Path("characterId") id: Long,
        @Query("ts") ts: Long,
        @Query("apikey") apikey: String,
        @Query("hash") hash: String
    ): Single<ResponseResult>
}