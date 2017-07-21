package com.tomaszstankowski.wykopapi

import android.arch.persistence.room.Room
import com.tomaszstankowski.wykopapi.persistence.WykopDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton


@Module class PersistenceModule {
    @Provides @Named("database_name") fun dbName() = "WykopDatabase"

    @Provides @Singleton fun db(app: App, @Named("database_name") name: String): WykopDatabase {
        return Room.databaseBuilder(app, WykopDatabase::class.java, name).build()
    }

    @Provides @Singleton fun linkDao(db: WykopDatabase) = db.linkDao()

    @Provides @Singleton fun userDao(db: WykopDatabase) = db.userDao()
}