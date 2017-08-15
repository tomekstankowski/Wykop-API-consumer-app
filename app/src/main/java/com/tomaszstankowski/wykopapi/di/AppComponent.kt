package com.tomaszstankowski.wykopapi.di

import com.tomaszstankowski.wykopapi.viemodel.LinkViewModel
import com.tomaszstankowski.wykopapi.viemodel.PromotedViewModel
import com.tomaszstankowski.wykopapi.viemodel.UserViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton @Component(modules = arrayOf(
        AppModule::class,
        ServiceModule::class,
        PersistenceModule::class
))
interface AppComponent {
    fun inject(viewModel: PromotedViewModel)
    fun inject(viewModel: LinkViewModel)
    fun inject(viewModel: UserViewModel)
}