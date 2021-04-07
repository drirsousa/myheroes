package br.com.rosait.myheroes.common.di

import br.com.rosait.myheroes.common.repository.DatabaseRepository
import br.com.rosait.myheroes.service.ItemService
import br.com.rosait.myheroes.view.home.favorite.FavoriteActivity
import br.com.rosait.myheroes.view.detail.ItemDetailActivity
import br.com.rosait.myheroes.view.home.main.MainActivity
import br.com.rosait.myheroes.viewmodel.ItemViewModel
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