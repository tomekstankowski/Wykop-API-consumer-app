package com.tomaszstankowski.wykopapi.persistence

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.tomaszstankowski.wykopapi.model.Comment
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.model.PromotedLink

@Dao interface LinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(link: Link)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(link: PromotedLink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(comment: Comment)

    @Query("DELETE FROM promoted")
    fun removePromoted()

    @Query("SELECT * FROM links WHERE id = :arg0")
    fun getLink(id: Int): LiveData<Link>

    @Query("SELECT links.* FROM links" +
            " INNER JOIN promoted ON links.id = promoted.link_id" +
            " ORDER BY vote_count DESC")
    fun getPromoted(): LiveData<List<Link>>

    @Query("SELECT * FROM comments WHERE link_id = :arg0")
    fun getComments(linkId: Int): LiveData<List<Comment>>
}