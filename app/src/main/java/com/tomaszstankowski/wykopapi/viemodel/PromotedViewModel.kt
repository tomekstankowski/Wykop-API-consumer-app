package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.LiveData
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.event.link_list.LastPageReached
import com.tomaszstankowski.wykopapi.event.link_list.LinkListLoadError
import com.tomaszstankowski.wykopapi.event.link_list.LinkListLoadSuccess
import com.tomaszstankowski.wykopapi.event.link_list.LinksNotFound
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.repository.NoDataExistsError

class PromotedViewModel(app: Application) : LinkListViewModel(app) {
    init {
        (app as App).component.inject(this)
    }

    override val onPageLoadSuccess = {
        isLoading.value = false
        page++
        bus.post(LinkListLoadSuccess())
    }

    override val onPageLoadFailure = { t: Throwable ->
        isLoading.value = false
        when (t) {
            is NoDataExistsError -> {
                hasMorePages = false; bus.post(LastPageReached())
            }
            else -> bus.post(LinkListLoadError())
        }
    }

    override val onPageRefreshSuccess = {
        isLoading.value = false
        hasMorePages = true
        page = 1
        bus.post(LinkListLoadSuccess())
    }

    override val onPageRefreshFailure = { t: Throwable ->
        isLoading.value = false
        when (t) {
            is NoDataExistsError -> {
                bus.post(LinksNotFound())
            }
            else -> bus.post(LinkListLoadError())
        }
    }

    override val links: LiveData<List<Link>> by lazy {
        isLoading.value = true
        repository.getPromotedLinks(onPageRefreshSuccess, onPageRefreshFailure)
    }

    override fun loadNextPage() {
        isLoading.value = true
        repository.loadPromoted(page + 1, onPageLoadSuccess, onPageLoadFailure)
    }

    override fun refresh() {
        isLoading.value = true
        repository.refreshPromoted(onPageRefreshSuccess, onPageRefreshFailure)
    }
}