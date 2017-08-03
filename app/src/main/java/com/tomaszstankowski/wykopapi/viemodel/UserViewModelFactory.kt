package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class UserViewModelFactory(private val app: Application, private val username: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(app, username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}