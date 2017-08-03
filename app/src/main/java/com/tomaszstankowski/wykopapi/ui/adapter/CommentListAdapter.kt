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
import com.tomaszstankowski.wykopapi.model.Link
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class CommentListAdapter(private val context: Context, private val headerLayout: Int = R.layout.link,
                         private val itemLayout: Int = R.layout.comment)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val TYPE_HEADER = 0
        val TYPE_ITEM = 1
    }

    var header: Link? = null
        set(value) {
            field = value; notifyItemChanged(0)
        }

    var emptyView: View? = null

    private val items: ArrayList<Comment> = ArrayList()

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    var onUserClickListener: OnUserClickListener? = null

    var onLinkClickListener: OnLinkClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                TYPE_HEADER -> {
                    val view = inflater.inflate(headerLayout, parent, false)
                    HeaderHolder(view)
                }
                TYPE_ITEM -> {
                    val view = inflater.inflate(itemLayout, parent, false)
                    ItemHolder(view)
                }
                else -> throw RuntimeException("No view type found to inflate ViewHolder.")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is HeaderHolder -> {
                Glide.with(context)
                        .fromString()
                        .load(header?.thumbnail)
                        .into(holder.thumbnail)
                holder.thumbnail.setOnClickListener { onLinkClickListener?.onClick(header!!) }
                holder.title.text = header?.title
                holder.title.setOnClickListener { onLinkClickListener?.onClick(header!!) }
                holder.desc.text = header?.description
                holder.desc.setOnClickListener { onLinkClickListener?.onClick(header!!) }
                holder.tags.text = header?.tags
                holder.author.text = header?.author
                holder.author.setOnClickListener { onUserClickListener?.onClick(header!!.author) }
                holder.date.text = header?.date
                holder.digCount.text = context.getString(R.string.dig_count, header?.digCount)
                holder.buryCount.text = context.getString(R.string.bury_count, header?.buryCount)
                holder.commentCount.text = context.getString(R.string.comment_count, header?.commentCount)
            }
            is ItemHolder -> {
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                //this comment is response to other comment
                if (items[position - 1].hasParent()) {
                    //make an indent
                    params.setMargins(40, 0, 0, 0)
                } else {
                    params.setMargins(0, 0, 0, 0)
                }
                holder.parent.layoutParams = params
                Glide.with(context)
                        .fromString()
                        .load(items[position - 1].authorAvatar)
                        .placeholder(R.drawable.thumbnail_placeholder)
                        .into(holder.avatar)
                holder.avatar.setOnClickListener { onUserClickListener?.onClick(items[position - 1].author) }
                holder.author.text = items[position - 1].author
                holder.author.setOnClickListener { onUserClickListener?.onClick(items[position - 1].author) }
                holder.date.text = items[position - 1].date
                holder.plusCount.text = context.getString(
                        R.string.comment_plus_count,
                        items[position - 1].plusCount)
                holder.minusCount.text = context.getString(
                        R.string.comment_minus_count,
                        Math.abs(items[position - 1].minusCount))
                holder.body.text = items[position - 1].body
            }
        }
    }

    override fun getItemViewType(position: Int) = if (position == 0) TYPE_HEADER else TYPE_ITEM

    override fun getItemCount() = items.size + 1

    fun setItems(items: Collection<Comment>) {
        Single.create<List<Comment>> {
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

    fun removeItems() {
        items.clear()
        notifyDataSetChanged()
        invalidateEmptyView()
    }

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail = view.findViewById(R.id.link_thumbnail) as ImageView
        val title = view.findViewById(R.id.link_title_tv) as TextView
        val desc = view.findViewById(R.id.link_desc_tv) as TextView
        val tags = view.findViewById(R.id.link_tags_tv) as TextView
        val author = view.findViewById(R.id.link_author_tv) as TextView
        val date = view.findViewById(R.id.link_date_tv) as TextView
        val digCount = view.findViewById(R.id.link_dig_count) as TextView
        val buryCount = view.findViewById(R.id.link_bury_count) as TextView
        val commentCount = view.findViewById(R.id.link_comment_count) as TextView
    }

    class ItemHolder(val parent: View) : RecyclerView.ViewHolder(parent) {
        val avatar = parent.findViewById(R.id.comment_avatar_iv) as ImageView
        val author = parent.findViewById(R.id.comment_author_tv) as TextView
        val date = parent.findViewById(R.id.comment_date_tv) as TextView
        val plusCount = parent.findViewById(R.id.comment_plus_count_tv) as TextView
        val minusCount = parent.findViewById(R.id.comment_minus_count_tv) as TextView
        val body = parent.findViewById(R.id.comment_body_tv) as TextView
    }

    private fun invalidateEmptyView() {
        if (items.isEmpty())
            emptyView?.visibility = View.VISIBLE
        else
            emptyView?.visibility = View.GONE
    }

    interface OnLinkClickListener {
        fun onClick(link: Link)
    }

    interface OnUserClickListener {
        fun onClick(user: String)
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