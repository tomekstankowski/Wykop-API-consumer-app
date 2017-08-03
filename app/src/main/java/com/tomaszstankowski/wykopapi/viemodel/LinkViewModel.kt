package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.squareup.otto.Bus
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.event.link.LinkLoadError
import com.tomaszstankowski.wykopapi.event.link.LinkLoadSuccess
import com.tomaszstankowski.wykopapi.event.link.LinkNotFound
import com.tomaszstankowski.wykopapi.event.link.comments.CommentListEmpty
import com.tomaszstankowski.wykopapi.event.link.comments.CommentListLoadError
import com.tomaszstankowski.wykopapi.event.link.comments.CommentListLoadSuccess
import com.tomaszstankowski.wykopapi.model.Comment
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.repository.LinkRepository
import com.tomaszstankowski.wykopapi.repository.NoDataExistsError
import javax.inject.Inject
import javax.inject.Named


class LinkViewModel(app: Application, val linkId: Int) : AndroidViewModel(app) {
    init {
        (app as App).component.inject(this)
    }

    @Inject @field:[Named("link")] lateinit var bus: Bus
    @Inject lateinit var repository: LinkRepository

    private val onLinkLoadSuccess: () -> Unit = {
        isLinkLoading.value = false
        bus.post(LinkLoadSuccess())
    }

    private val onLinkLoadError: (Throwable) -> Unit = { t ->
        isLinkLoading.value = false
        when (t) {
            is NoDataExistsError -> bus.post(LinkNotFound())
            else -> bus.post(LinkLoadError())
        }
    }

    private val onCommentsLoadSuccess: () -> Unit = {
        isCommentListLoading.value = false
        bus.post(CommentListLoadSuccess())
    }

    private val onCommentsLoadError: (Throwable) -> Unit = { t ->
        isCommentListLoading.value = false
        when (t) {
            is NoDataExistsError -> bus.post(CommentListEmpty())
            else -> bus.post(CommentListLoadError())
        }
    }

    val isLinkLoading = MutableLiveData<Boolean>()

    val isCommentListLoading = MutableLiveData<Boolean>()

    val link: LiveData<Link> by lazy {
        isLinkLoading.value = true
        repository.getLink(linkId, onLinkLoadSuccess, onLinkLoadError)
    }

    val comments: LiveData<List<Comment>> by lazy {
        isCommentListLoading.value = true
        repository.getComments(linkId, onCommentsLoadSuccess, onCommentsLoadError)
    }

    fun refresh() {
        isLinkLoading.value = true
        isCommentListLoading.value = true
        repository.refreshLink(linkId, onLinkLoadSuccess, onLinkLoadError)
        repository.refreshComments(linkId, onCommentsLoadSuccess, onCommentsLoadError)
    }
}