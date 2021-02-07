package br.com.rosait.marvelcharacters.common.repository

import android.content.Context
import br.com.rosait.marvelcharacters.BuildConfig
import br.com.rosait.marvelcharacters.common.interceptor.ConnectivityInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestManager {

    companion object {

        fun createHttpClient(context: Context, timeout: Long) : OkHttpClient.Builder {
            val client = OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .addInterceptor(ConnectivityInterceptor(context))

            if(BuildConfig.DEBUG) {
                val logInterceptor = HttpLoggingInterceptor()
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(logInterceptor)
            }

            return client
        }

        fun getEndpoint(context: Context) : Api {

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(createHttpClient(context, 60).build())
                .build()

            return retrofit.create(Api::class.java)
        }
    }
}