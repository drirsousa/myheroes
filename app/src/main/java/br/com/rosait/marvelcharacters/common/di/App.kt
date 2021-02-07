package br.com.rosait.marvelcharacters.common.di

import android.app.Application

class App : Application() {

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