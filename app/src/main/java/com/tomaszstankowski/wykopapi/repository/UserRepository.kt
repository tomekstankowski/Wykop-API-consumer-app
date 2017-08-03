package com.tomaszstankowski.wykopapi.repository

import android.arch.lifecycle.LiveData
import com.tomaszstankowski.wykopapi.model.User
import com.tomaszstankowski.wykopapi.persistence.UserDao
import com.tomaszstankowski.wykopapi.service.Service
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton class UserRepository
@Inject constructor(private val service: Service, private val dao: UserDao) {

    fun getUser(name: String,
                onComplete: () -> Unit = {},
                onError: (Throwable) -> Unit = {}): LiveData<User> {
        refreshUser(name, onComplete, onError)
        return dao.get(name)
    }

    fun refreshUser(name: String,
                    onComplete: () -> Unit = {},
                    onError: (Throwable) -> Unit = {}) {
        Completable.create({
            val response = service.getUser(name).execute()
            if (response.isSuccessful) {
                val user = response.body()
                if (user == null || user.login == "") {
                    it.onError(NoDataExistsError())
                } else {
                    dao.save(user)
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