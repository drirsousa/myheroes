package br.com.rosait.marvelcharacters.common.di

import br.com.rosait.marvelcharacters.common.repository.DatabaseRepository
import br.com.rosait.marvelcharacters.service.ItemService
import br.com.rosait.marvelcharacters.view.FavoriteActivity
import br.com.rosait.marvelcharacters.view.ItemDetailActivity
import br.com.rosait.marvelcharacters.view.MainActivity
import br.com.rosait.marvelcharacters.viewmodel.ItemViewModel
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(app: App)

    fun inject(itemViewModel: ItemViewModel)

    fun inject(itemService: ItemService)

    fun inject(databaseRepository: DatabaseRepository)

    fun inject(mainActivity: MainActivity)

    fun inject(favoriteActivity: FavoriteActivity)

    fun inject(itemDetailActivity: ItemDetailActivity)
}