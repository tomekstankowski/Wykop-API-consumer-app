package com.tomaszstankowski.wykopapi.persistence


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.tomaszstankowski.wykopapi.model.Comment

import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.model.PromotedLink
import com.tomaszstankowski.wykopapi.model.User

@Database(entities = arrayOf(User::class, Link::class, PromotedLink::class, Comment::class),
        version = 1)
@TypeConverters(Converter::class)
abstract class WykopDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
    abstract fun userDao(): UserDao
}
