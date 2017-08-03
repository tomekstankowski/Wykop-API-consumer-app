package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.squareup.otto.Bus
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.event.user.UserLoadError
import com.tomaszstankowski.wykopapi.event.user.UserLoadSuccess
import com.tomaszstankowski.wykopapi.event.user.UserNotFound
import com.tomaszstankowski.wykopapi.model.User
import com.tomaszstankowski.wykopapi.repository.NoDataExistsError
import com.tomaszstankowski.wykopapi.repository.UserRepository
import javax.inject.Inject
import javax.inject.Named


class UserViewModel(app: Application, val username: String) : AndroidViewModel(app) {
    init {
        (app as App).component.inject(this)
    }

    @Inject @field:[Named("user")] lateinit var bus: Bus
    @Inject lateinit var repository: UserRepository

    val onLoadSuccess: () -> Unit = {
        isLoading.value = false
        bus.post(UserLoadSuccess())
    }

    val onLoadFailure: (Throwable) -> Unit = { t ->
        isLoading.value = false
        when (t) {
            is NoDataExistsError -> bus.post(UserNotFound())
            else -> bus.post(UserLoadError())
        }
    }

    val isLoading = MutableLiveData<Boolean>()

    val user: LiveData<User> by lazy {
        isLoading.value = true
        repository.getUser(username, onLoadSuccess, onLoadFailure)
    }

    fun refresh() {
        isLoading.value = true
        repository.refreshUser(username, onLoadSuccess, onLoadFailure)
    }
}