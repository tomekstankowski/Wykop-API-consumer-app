package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.repository.LinkRepository
import javax.inject.Inject

abstract class LinkListViewModel(app: Application) : AndroidViewModel(app) {
    @Inject protected lateinit var repository: LinkRepository
    abstract val links: LiveData<List<Link>>
    val linksStatus: MutableLiveData<ResourceStatus> = MutableLiveData()

    abstract val onPageLoadSuccess: () -> Unit
    abstract val onPageLoadFailure: (Throwable) -> Unit
    abstract val onRefreshSuccess: () -> Unit
    abstract val onRefreshFailure: (Throwable) -> Unit

    var page = 1
    var hasMorePages = true

    abstract fun loadNextPage()

    abstract fun refresh()
}