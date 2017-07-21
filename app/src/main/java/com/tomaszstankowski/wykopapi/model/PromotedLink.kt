package com.tomaszstankowski.wykopapi.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "promoted",
        foreignKeys = arrayOf(
                ForeignKey(entity = Link::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("link_id"))
        ))
class PromotedLink(@PrimaryKey @ColumnInfo(name = "link_id") var linkId: Int = 0)
