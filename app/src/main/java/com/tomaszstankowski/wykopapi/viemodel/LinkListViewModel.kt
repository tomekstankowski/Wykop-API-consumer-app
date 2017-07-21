package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.squareup.otto.Bus
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.repository.LinkRepository
import javax.inject.Inject
import javax.inject.Named

abstract class LinkListViewModel(app: Application) : AndroidViewModel(app) {
    @Inject protected lateinit var repository: LinkRepository
    @Inject @field:[Named("link_list")] protected lateinit var bus: Bus

    abstract val links: LiveData<List<Link>>
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    abstract val onPageLoadSuccess: () -> Unit
    abstract val onPageLoadFailure: (Throwable) -> Unit
    abstract val onPageRefreshSuccess: () -> Unit
    abstract val onPageRefreshFailure: (Throwable) -> Unit

    var page = 1
    var hasMorePages = true

    abstract fun loadNextPage()

    abstract fun refresh()
}