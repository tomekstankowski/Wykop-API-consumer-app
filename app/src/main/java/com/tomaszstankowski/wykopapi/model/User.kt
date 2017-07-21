package com.tomaszstankowski.wykopapi.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users",
        indices = arrayOf(
                Index(value = "name"),
                Index(value = "rank")
        ))
class User(
        @PrimaryKey var username: String = "",
        var name: String = "",
        var rank: Int = 0,
        @ColumnInfo(name = "links_added") @SerializedName("links_added") var linksAdded: Int = 0,
        var followers: Int = 0,
        var avatar: String = ""
)