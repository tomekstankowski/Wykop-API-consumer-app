package com.tomaszstankowski.wykopapi.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "links",
        indices = arrayOf(
                Index(value = "title"),
                Index(value = "description"),
                Index(value = "tags"),
                Index(value = "author")
        ))
class Link(
        @PrimaryKey var id: Int = 0,
        var title: String = "",
        var description: String = "",
        var tags: String = "",
        @ColumnInfo(name = "source_url") @SerializedName("source_url") var src: String = "",
        var author: String = "",
        var date: String = "",
        @ColumnInfo(name = "vote_count") @SerializedName("vote_count") var digCount: Int = 0,
        @ColumnInfo(name = "report_count") @SerializedName("report_count") var buryCount: Int = 0,
        @ColumnInfo(name = "comment_count") @SerializedName("comment_count") var commentCount: Int = 0,
        @ColumnInfo(name = "preview") @SerializedName("preview") var thumbnail: String? = null
)