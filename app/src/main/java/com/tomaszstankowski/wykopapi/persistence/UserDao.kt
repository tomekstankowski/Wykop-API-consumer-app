package com.tomaszstankowski.wykopapi.persistence

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.tomaszstankowski.wykopapi.model.User


@Dao interface UserDao {
    @Query("SELECT * FROM users WHERE username = :arg0")
    fun get(username: String): LiveData<User>

    @Insert
    fun save(user: User)
}