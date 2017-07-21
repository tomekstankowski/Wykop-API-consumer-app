package com.tomaszstankowski.wykopapi.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "comments",
        indices = arrayOf(
                Index(value = "author"),
                Index(value = "link_id"),
                Index(value = "parent_id")
        )
)
class Comment(
        @PrimaryKey var id: Int = 0,
        var author: String = "",
        var date: String = "",
        var body: String = "",
        @ColumnInfo(name = "vote_count_plus") @SerializedName("vote_count_plus") var plusCount: Int = 0,
        @ColumnInfo(name = "vote_count_minus") @SerializedName("vote_count_minus") var minusCount: Int = 0,
        @ColumnInfo(name = "author_avatar") @SerializedName("author_avatar") var authorAvatar: String? = null,
        @ColumnInfo(name = "parent_id") @SerializedName("parent_id") var parentId: Int? = null,
        @ColumnInfo(name = "link_id") @SerializedName("link_id") var linkId: Int = 0
)