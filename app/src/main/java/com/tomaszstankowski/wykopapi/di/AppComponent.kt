package com.tomaszstankowski.wykopapi.di

import com.tomaszstankowski.wykopapi.ui.activity.LinkActivity
import com.tomaszstankowski.wykopapi.ui.activity.UserActivity
import com.tomaszstankowski.wykopapi.ui.fragment.PromotedFragment
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
    fun inject(fragment: PromotedFragment)
    fun inject(activity: LinkActivity)
    fun inject(viewModel: LinkViewModel)
    fun inject(activity: UserActivity)
    fun inject(viewModel: UserViewModel)
}