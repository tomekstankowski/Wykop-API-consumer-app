package com.tomaszstankowski.wykopapi.ui.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.model.Link


class LinkListAdapter(private val context: Context, private val layoutId: Int = R.layout.link_preview)
    : RecyclerView.Adapter<LinkListAdapter.LinkHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val items: ArrayList<Link> = ArrayList()

    var emptyView: View? = null

    var onClickListener: OnClickListener? = null

    class LinkHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.link_preview_thumbnail_iv)
        val title: TextView = view.findViewById(R.id.link_preview_title_tv)
        val tags: TextView = view.findViewById(R.id.link_preview_tags_tv)
        val digCount: TextView = view.findViewById(R.id.link_preview_digs_tv)
        val buryCount: TextView = view.findViewById(R.id.link_preview_buries_tv)
        val commentCount: TextView = view.findViewById(R.id.link_preview_comment_count_tv)
        val cardView: CardView = view.findViewById(R.id.link_preview_card_view)
    }

    interface OnClickListener {
        fun onItemClicked(position: Int, link: Link)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LinkHolder {
        val view = inflater.inflate(layoutId, parent, false)
        return LinkHolder(view)
    }

    override fun onBindViewHolder(holder: LinkHolder?, position: Int) {
        if (holder == null)
            return
        Glide.with(context)
                .fromString()
                .load(items[position].thumbnail)
                .placeholder(R.drawable.thumbnail_placeholder)
                .into(holder.image)
        holder.title.text = items[position].title
        holder.tags.text = items[position].tags
        holder.digCount.text = context.getString(R.string.dig_count, items[position].digCount)
        holder.buryCount.text = context.getString(R.string.bury_count, items[position].buryCount)
        holder.commentCount.text = context.getString(R.string.comment_count, items[position].commentCount)
        holder.cardView.setOnClickListener { onClickListener?.onItemClicked(position, items[position]) }
    }

    override fun getItemCount() = items.size

    fun setItems(items: Collection<Link>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
        invalidateEmptyView()
    }

    fun removeItems() {
        items.clear()
        notifyDataSetChanged()
        invalidateEmptyView()
    }

    private fun invalidateEmptyView() {
        if (items.isEmpty())
            emptyView?.visibility = View.VISIBLE
        else
            emptyView?.visibility = View.GONE
    }
}