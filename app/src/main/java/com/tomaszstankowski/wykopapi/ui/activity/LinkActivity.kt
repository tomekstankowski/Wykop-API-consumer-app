package com.tomaszstankowski.wykopapi.ui.activity

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.bumptech.glide.Glide
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.ui.adapter.CommentListAdapter
import com.tomaszstankowski.wykopapi.viemodel.LinkViewModel
import com.tomaszstankowski.wykopapi.viemodel.LinkViewModelFactory
import com.tomaszstankowski.wykopapi.viemodel.ResourceStatus

/**
 * Displays link and its comments in a recyclerview.
 */
class LinkActivity : AppCompatActivity(),
        LifecycleRegistryOwner,
        CommentListAdapter.OnUserClickListener {

    val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry

    private lateinit var viewModel: LinkViewModel

    private val toolbar: Toolbar by bindView(R.id.activity_link_toolbar)
    private val recyclerView: RecyclerView by bindView(R.id.activity_link_recycler_view)
    private val linkContainer: View by bindView(R.id.link_container)
    private val linkNotFoundContainer: View by bindView(R.id.link_not_found_container)
    private val linkProgress: ProgressBar by bindView(R.id.link_progressbar)
    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.activity_link_swipe_refresh_layout)
    private val thumbnail: ImageView by bindView(R.id.link_thumbnail)
    private val title: TextView by bindView(R.id.link_title_tv)
    private val desc: TextView by bindView(R.id.link_desc_tv)
    private val tags: TextView by bindView(R.id.link_tags_tv)
    private val author: TextView by bindView(R.id.link_author_tv)
    private val date: TextView by bindView(R.id.link_date_tv)
    private val digCount: TextView by bindView(R.id.link_dig_count)
    private val buryCount: TextView by bindView(R.id.link_bury_count)
    private val commentCount: TextView by bindView(R.id.link_comment_count)

    private lateinit var adapter: CommentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link)
        setActionBar()
        setRecyclerview()
        setViewModel()
    }

    private fun setActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.link)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setRecyclerview() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = CommentListAdapter(this)
        recyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
    }

    private fun setViewModel() {
        val linkId = intent.getIntExtra(LINK_ID, -1)
        if (linkId != -1) {
            val factory = LinkViewModelFactory(application, linkId)
            viewModel = ViewModelProviders.of(this, factory).get(LinkViewModel::class.java)
            viewModel.link.observe(this, Observer { displayLink(it) })
            viewModel.comments.observe(this, Observer { adapter.setItems(it) })
            viewModel.commentsStatus.observe(this, Observer {
                swipeRefreshLayout.isRefreshing = it == ResourceStatus.LOADING
                if (it == ResourceStatus.ERROR) {
                    Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show()
                }
            })
            viewModel.linkStatus.observe(this, Observer {
                linkProgress.visibility =
                        if (it == ResourceStatus.LOADING)
                            View.VISIBLE
                        else
                            View.GONE
                if (it == ResourceStatus.NOT_FOUND) {
                    linkContainer.visibility = View.INVISIBLE
                    linkNotFoundContainer.visibility = View.VISIBLE
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed(); return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        adapter.onUserClickListener = this
    }

    override fun onPause() {
        super.onPause()
        adapter.onUserClickListener = null
    }

    private fun displayLink(link: Link?) {
        if (link == null) {
            linkContainer.visibility = View.INVISIBLE
        } else {
            linkContainer.visibility = View.VISIBLE
            Glide.with(this)
                    .fromString()
                    .load(link.thumbnail)
                    .into(thumbnail)
            thumbnail.setOnClickListener(this::onLinkClicked)
            title.text = link.title
            title.setOnClickListener(this::onLinkClicked)
            desc.text = link.description
            desc.setOnClickListener(this::onLinkClicked)
            tags.text = link.tags
            date.text = link.date
            author.text = link.author
            author.setOnClickListener { onUserClicked(link.author) }
            commentCount.text = getString(R.string.comment_count, link.commentCount)
            digCount.text = getString(R.string.dig_count, link.digCount)
            buryCount.text = getString(R.string.bury_count, link.buryCount)
        }
    }

    override fun onUserClicked(username: String) {
        UserActivity.start(this, username)
    }

    fun onLinkClicked(view: View) {
        val url = viewModel.link.value?.src
        if (url != null)
            PreviewActivity.start(this, url)
    }

    companion object {
        val LINK_ID: String = "LINK_ID"

        fun start(context: Context, linkId: Int) {
            val intent = Intent(context, LinkActivity::class.java)
            intent.putExtra(LINK_ID, linkId)
            context.startActivity(intent)
        }
    }
}