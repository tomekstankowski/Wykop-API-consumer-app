package com.tomaszstankowski.wykopapi.di

import com.tomaszstankowski.wykopapi.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class AppModule(val app: App) {
    @Provides @Singleton fun app() = app
}