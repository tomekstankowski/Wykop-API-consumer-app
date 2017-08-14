package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.model.User
import com.tomaszstankowski.wykopapi.repository.NoDataExistsError
import com.tomaszstankowski.wykopapi.repository.UserRepository
import javax.inject.Inject


class UserViewModel(app: Application, val username: String) : AndroidViewModel(app) {
    init {
        (app as App).component.inject(this)
    }

    @Inject lateinit var repository: UserRepository

    val onLoadSuccess: () -> Unit = {
        userStatus.value = ResourceStatus.OK
    }

    val onLoadFailure: (Throwable) -> Unit = { t ->
        userStatus.value =
        when (t) {
            is NoDataExistsError -> ResourceStatus.NOT_FOUND
            else -> ResourceStatus.ERROR
        }
    }

    val userStatus = MutableLiveData<ResourceStatus>()

    val user: LiveData<User> by lazy {
        userStatus.value = ResourceStatus.LOADING
        repository.getUser(username, onLoadSuccess, onLoadFailure)
    }

    fun refresh() {
        userStatus.value = ResourceStatus.LOADING
        repository.refreshUser(username, onLoadSuccess, onLoadFailure)
    }
}