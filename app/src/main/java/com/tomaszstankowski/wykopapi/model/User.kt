package com.tomaszstankowski.wykopapi.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
class User(
        @PrimaryKey var login: String = "",
        var name: String = "",
        var rank: Int = 0,
        @ColumnInfo(name = "links_added") @SerializedName("links_added") var linksAdded: Int = 0,
        @ColumnInfo(name = "signup_date") @SerializedName("signup_date") var signupDate: String = "",
        var sex: String = "",
        var followers: Int = 0,
        var about: String = "",
        var city: String = "",
        @SerializedName("avatar_big") var avatar: String = ""
)