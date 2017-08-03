package com.tomaszstankowski.wykopapi.di

import com.squareup.otto.Bus
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * ViewModels do not know anything about Views.
 * When they finish their actions they post events to Event Bus.
 * To avoid conflicts, each View - ViewModel pair gets it's own bus.
 */
@Module class EventModule {

    @Provides @Singleton @Named("link_list") fun linkListBus() = Bus("link_list")
    @Provides @Singleton @Named("link") fun linkBus() = Bus("link")
    @Provides @Singleton @Named("user") fun userBus() = Bus("user")

}