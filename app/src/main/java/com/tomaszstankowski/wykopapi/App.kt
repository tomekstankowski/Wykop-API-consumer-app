package com.tomaszstankowski.wykopapi

import android.app.Application
import com.tomaszstankowski.wykopapi.di.AppComponent
import com.tomaszstankowski.wykopapi.di.AppModule
import com.tomaszstankowski.wykopapi.di.PersistenceModule
import com.tomaszstankowski.wykopapi.di.ServiceModule


class App : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .serviceModule(ServiceModule())
                .persistenceModule(PersistenceModule())
                .build()
    }
}