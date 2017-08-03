package com.tomaszstankowski.wykopapi.repository

import android.arch.lifecycle.LiveData
import com.tomaszstankowski.wykopapi.model.Comment
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.model.PromotedLink
import com.tomaszstankowski.wykopapi.persistence.LinkDao
import com.tomaszstankowski.wykopapi.persistence.WykopDatabase
import com.tomaszstankowski.wykopapi.service.Service
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository stands as the single source of truth.
 * The data comes from the wykopAPI and is persisted in db.
 */
@Singleton class LinkRepository
@Inject constructor(private val service: Service,
                    private val dao: LinkDao,
                    private val db: WykopDatabase) {

    fun getPromotedLinks(onComplete: () -> Unit = {},
                         onError: (Throwable) -> Unit = {}): LiveData<List<Link>> {

        refreshPromoted(onComplete, onError)
        return dao.getPromoted()
    }

    fun getLink(id: Int,
                onComplete: () -> Unit = {},
                onError: (Throwable) -> Unit = {}): LiveData<Link> {

        refreshLink(id, onComplete, onError)
        return dao.getLink(id)
    }

    fun getComments(linkId: Int,
                    onComplete: () -> Unit = {},
                    onError: (Throwable) -> Unit = {}): LiveData<List<Comment>> {
        refreshComments(linkId, onComplete, onError)
        return dao.getComments(linkId)
    }

    fun refreshLink(id: Int,
                    onComplete: () -> Unit = {},
                    onError: (Throwable) -> Unit = {}) {

        Completable.create({
            val response = service.getLink(id).execute()
            if (response.isSuccessful) {
                val link = response.body()
                if (link == null || link.id == -1) {
                    it.onError(NoDataExistsError())
                } else {
                    dao.save(link)
                    it.onComplete()
                }
            } else {
                it.onError(ServiceError())
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete, onError)
    }

    fun loadPromoted(page: Int,
                     onComplete: () -> Unit = {},
                     onError: (Throwable) -> Unit = {}) {

        Completable.create({
            val response = service.getPromotedLinks(page).execute()
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null || body.isEmpty()) {
                    it.onError(NoDataExistsError())
                } else {
                    db.beginTransaction()
                    try {
                        for (link in body) {
                            dao.save(link)
                            dao.save(PromotedLink(link.id))
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                    it.onComplete()
                }
            } else {
                it.onError(ServiceError())
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete, onError)
    }

    fun refreshPromoted(onComplete: () -> Unit = {},
                        onError: (Throwable) -> Unit = {}) {
        Completable.create({
            val response = service.getPromotedLinks(0).execute()
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null || body.isEmpty()) {
                    it.onError(NoDataExistsError())
                } else {
                    db.beginTransaction()
                    try {
                        dao.removePromoted()
                        for (link in body) {
                            dao.save(link)
                            dao.save(PromotedLink(link.id))
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                    it.onComplete()
                }
            } else {
                it.onError(ServiceError())
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete, onError)
    }

    fun refreshComments(linkId: Int,
                        onComplete: () -> Unit = {},
                        onError: (Throwable) -> Unit = {}) {
        Completable.create({
            val response = service.getComments(linkId).execute()
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null || body.isEmpty()) {
                    it.onError(NoDataExistsError())
                } else {
                    for (comment in body) {
                        comment.linkId = linkId
                        dao.save(comment)
                    }
                    it.onComplete()
                }
            } else {
                it.onError(ServiceError())
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete, onError)
    }
}