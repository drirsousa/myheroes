package br.com.rosait.marvelcharacters.common.di

import android.content.Context
import br.com.rosait.marvelcharacters.common.repository.ApiRepository
import br.com.rosait.marvelcharacters.common.repository.DatabaseRepository
import br.com.rosait.marvelcharacters.common.database.AppDatabase
import br.com.rosait.marvelcharacters.service.ItemService
import br.com.rosait.marvelcharacters.viewmodel.ItemViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: App, val context: Context) {

    @Provides @Singleton
    fun provideApplication() = app

    @Provides
    fun provideContext() = context

    @Provides
    fun provideItemService(context: Context) : ItemService {
        return ItemService(context)
    }

    @Provides
    fun provideApiRepository(context: Context) : ApiRepository {
        return ApiRepository(context)
    }

    @Provides
    fun provideDatabaseRepository(context: Context) : DatabaseRepository {
        return DatabaseRepository(context)
    }

    @Provides
    fun provideAppDatabase(context: Context) : AppDatabase {
        return AppDatabase.getDatabaseBuilder(context)
    }

    @Provides
    fun provideItemViewModel(context: Context) : ItemViewModel {
        return ItemViewModel(context)
    }
}