package com.tomaszstankowski.wykopapi

import android.app.Application


class App : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .serviceModule(ServiceModule())
                .persistenceModule(PersistenceModule())
                .build()
    }
}