package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.LiveData
import com.tomaszstankowski.wykopapi.App

import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.repository.NoDataExistsError

class PromotedViewModel(app: Application) : LinkListViewModel(app) {
    init {
        (app as App).component.inject(this)
    }

    override val onPageLoadSuccess = {
        page++
        linksStatus.value = ResourceStatus.OK
    }

    override val onPageLoadFailure = { t: Throwable ->
        linksStatus.value =
        when (t) {
            is NoDataExistsError -> {
                hasMorePages = false
                ResourceStatus.NOT_FOUND
            }
            else -> ResourceStatus.ERROR
        }
    }

    override val onRefreshSuccess = {
        linksStatus.value = ResourceStatus.OK
        hasMorePages = true
        page = 1
    }

    override val onRefreshFailure = { t: Throwable ->
        linksStatus.value =
        when (t) {
            is NoDataExistsError -> ResourceStatus.NOT_FOUND
            else -> ResourceStatus.ERROR
        }
    }

    override val links: LiveData<List<Link>> by lazy {
        linksStatus.value = ResourceStatus.LOADING
        repository.getPromotedLinks(onRefreshSuccess, onRefreshFailure)
    }

    override fun loadNextPage() {
        linksStatus.value = ResourceStatus.LOADING
        repository.loadPromoted(page + 1, onPageLoadSuccess, onPageLoadFailure)
    }

    override fun refresh() {
        linksStatus.value = ResourceStatus.LOADING
        repository.refreshPromoted(onRefreshSuccess, onRefreshFailure)
    }
}