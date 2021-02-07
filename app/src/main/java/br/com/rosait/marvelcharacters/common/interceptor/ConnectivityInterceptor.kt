package br.com.rosait.marvelcharacters.common.interceptor

import android.content.Context
import br.com.rosait.marvelcharacters.common.network.Networking.Companion.isConnected
import br.com.rosait.marvelcharacters.common.network.NoConnectivityException
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