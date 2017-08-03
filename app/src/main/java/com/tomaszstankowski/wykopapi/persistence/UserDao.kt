package com.tomaszstankowski.wykopapi.persistence

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.tomaszstankowski.wykopapi.model.User


@Dao interface UserDao {
    @Query("SELECT * FROM users WHERE login = :arg0")
    fun get(name: String): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(user: User)
}