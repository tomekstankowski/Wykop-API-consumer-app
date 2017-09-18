package com.tomaszstankowski.wykopapi.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.model.Comment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class CommentListAdapter(private val context: Context, private val layout: Int = R.layout.comment)
    : RecyclerView.Adapter<CommentListAdapter.CommentHolder>() {

    var emptyView: View? = null

    private val items: ArrayList<Comment> = ArrayList()

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    var onUserClickListener: OnUserClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CommentHolder {
        val view = inflater.inflate(layout, parent, false)
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder: CommentHolder?, position: Int) {
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                //this comment is response to other comment
        if (items[position].hasParent()) {
                    //make an indent
                    params.setMargins(40, 0, 0, 0)
                } else {
                    params.setMargins(0, 0, 0, 0)
                }
        holder?.parent?.layoutParams = params
                Glide.with(context)
                        .fromString()
                        .load(items[position].authorAvatar)
                        .placeholder(R.drawable.thumbnail_placeholder)
                        .into(holder?.avatar)
        holder?.avatar?.setOnClickListener { onUserClickListener?.onUserClicked(items[position].author) }
        holder?.author?.text = items[position].author
        holder?.author?.setOnClickListener { onUserClickListener?.onUserClicked(items[position].author) }
        holder?.date?.text = items[position].date
        holder?.plusCount?.text = context.getString(
                        R.string.comment_plus_count,
                items[position].plusCount)
        holder?.minusCount?.text = context.getString(
                        R.string.comment_minus_count,
                Math.abs(items[position].minusCount))
        holder?.body?.text = items[position].body
    }


    override fun getItemCount() = items.size

    fun setItems(items: Collection<Comment>?) {
        if (items == null) {
            removeItems()
        } else {
            Single.create<Collection<Comment>> {
                val sorted = sorted(items)
                it.onSuccess(sorted)
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ sorted ->
                        this.items.clear()
                        this.items.addAll(sorted)
                        notifyDataSetChanged()
                        invalidateEmptyView()
                    }
                    )
        }
    }

    fun removeItems() {
        items.clear()
        notifyDataSetChanged()
        invalidateEmptyView()
    }

    class CommentHolder(val parent: View) : RecyclerView.ViewHolder(parent) {
        val avatar: ImageView = parent.findViewById(R.id.comment_avatar_iv)
        val author: TextView = parent.findViewById(R.id.comment_author_tv)
        val date: TextView = parent.findViewById(R.id.comment_date_tv)
        val plusCount: TextView = parent.findViewById(R.id.comment_plus_count_tv)
        val minusCount: TextView = parent.findViewById(R.id.comment_minus_count_tv)
        val body: TextView = parent.findViewById(R.id.comment_body_tv)
    }

    private fun invalidateEmptyView() {
        if (items.isEmpty())
            emptyView?.visibility = View.VISIBLE
        else
            emptyView?.visibility = View.GONE
    }

    interface OnUserClickListener {
        fun onUserClicked(username: String)
    }
}

fun Comment.hasParent() = id != parentId

fun Comment.isChildOf(comment: Comment) = parentId == comment.id

fun sorted(comments: Collection<Comment>): List<Comment> {
    val parents = comments.filter { !it.hasParent() }.sortedByDescending { it.plusCount - it.minusCount }
    val sorted = LinkedList<Comment>()
    for (p in parents) {
        sorted += p
        val children = comments.filter { it.isChildOf(p) && it.hasParent() }.sortedBy { it.date }
        sorted += children
    }
    return sorted
}