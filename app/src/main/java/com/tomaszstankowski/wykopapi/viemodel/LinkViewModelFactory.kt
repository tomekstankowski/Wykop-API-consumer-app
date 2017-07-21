package com.tomaszstankowski.wykopapi.viemodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class LinkViewModelFactory(private val app: Application, private val linkId: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LinkViewModel::class.java)) {
            return LinkViewModel(app, linkId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}