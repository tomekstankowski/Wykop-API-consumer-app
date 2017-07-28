package com.tomaszstankowski.wykopapi.di

import com.tomaszstankowski.wykopapi.service.Webservice
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module class ServiceModule {
    @Provides @Named("appkey") fun appkey() = "3nQhH0C1ZN"

    @Provides @Singleton fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("http://a.wykop.pl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides @Singleton fun webservice(retrofit: Retrofit) = retrofit.create(Webservice::class.java)

}