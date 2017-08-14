package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.model.Comment
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.repository.LinkRepository
import com.tomaszstankowski.wykopapi.repository.NoDataExistsError
import javax.inject.Inject


class LinkViewModel(app: Application, val linkId: Int) : AndroidViewModel(app) {
    init {
        (app as App).component.inject(this)
    }

    @Inject lateinit var repository: LinkRepository

    private val onLinkLoadSuccess: () -> Unit = {
        linkStatus.value = ResourceStatus.OK
    }

    private val onLinkLoadError: (Throwable) -> Unit = { t ->
        linkStatus.value = when (t) {
            is NoDataExistsError -> ResourceStatus.NOT_FOUND
            else -> ResourceStatus.ERROR
        }
    }

    private val onCommentsLoadSuccess: () -> Unit = {
        commentsStatus.value = ResourceStatus.OK
    }

    private val onCommentsLoadFailure: (Throwable) -> Unit = { t ->
        commentsStatus.value = when (t) {
            is NoDataExistsError -> ResourceStatus.NOT_FOUND
            else -> ResourceStatus.ERROR
        }
    }

    val linkStatus = MutableLiveData<ResourceStatus>()

    val commentsStatus = MutableLiveData<ResourceStatus>()

    val link: LiveData<Link> by lazy {
        linkStatus.value = ResourceStatus.LOADING
        repository.getLink(linkId, onLinkLoadSuccess, onLinkLoadError)
    }

    val comments: LiveData<List<Comment>> by lazy {
        commentsStatus.value = ResourceStatus.LOADING
        repository.getComments(linkId, onCommentsLoadSuccess, onCommentsLoadFailure)
    }

    fun refresh() {
        linkStatus.value = ResourceStatus.LOADING
        commentsStatus.value = ResourceStatus.LOADING
        repository.refreshLink(linkId, onLinkLoadSuccess, onLinkLoadError)
        repository.refreshComments(linkId, onCommentsLoadSuccess, onCommentsLoadFailure)
    }
}