package br.com.rosait.myheroes.common.di

import android.app.Application

class App : Application() {

    companion object {
        var mLimit = 10
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this, applicationContext))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }
}