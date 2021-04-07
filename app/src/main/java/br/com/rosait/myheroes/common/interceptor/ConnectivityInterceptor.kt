package br.com.rosait.myheroes.common.interceptor

import android.content.Context
import br.com.rosait.myheroes.common.network.Networking.Companion.isConnected
import br.com.rosait.myheroes.common.network.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor(val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if(!isConnected(context))  throw NoConnectivityException()

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}