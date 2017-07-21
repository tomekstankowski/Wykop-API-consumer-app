package com.tomaszstankowski.wykopapi.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.bindView
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.event.link.LinkLoadError
import com.tomaszstankowski.wykopapi.event.link.LinkNotExists
import com.tomaszstankowski.wykopapi.event.link.comments.CommentListEmpty
import com.tomaszstankowski.wykopapi.event.link.comments.CommentListLoadError
import com.tomaszstankowski.wykopapi.ui.adapter.CommentListAdapter
import com.tomaszstankowski.wykopapi.viemodel.LinkViewModel
import com.tomaszstankowski.wykopapi.viemodel.LinkViewModelFactory
import javax.inject.Inject
import javax.inject.Named


class LinkActivity : LifecycleActivity() {
    @Inject @field:[Named("link")] lateinit var bus: Bus
    private lateinit var viewModel: LinkViewModel
    private val recyclerView: RecyclerView by bindView(R.id.activity_link_recycler_view)
    private val progressbar: ProgressBar by bindView(R.id.activity_link_progressbar)
    private lateinit var adapter: CommentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).component.inject(this)

        setContentView(R.layout.activity_link)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = CommentListAdapter(this)
        recyclerView.adapter = adapter

        val linkId = intent.getIntExtra(LINK_ID, -1)
        if (linkId != -1) {
            val factory = LinkViewModelFactory(application, linkId)
            viewModel = ViewModelProviders.of(this, factory).get(LinkViewModel::class.java)
            viewModel.link.observe(this, Observer {
                adapter.header = it
            })
            viewModel.comments.observe(this, Observer {
                if (it != null)
                    adapter.setItems(it)
            })
            viewModel.isCommentListLoading.observe(this, Observer {
                if (it != null)
                    if (it)
                        progressbar.visibility = View.VISIBLE
                    else
                        progressbar.visibility = View.GONE

            })
        }
    }

    override fun onResume() {
        super.onResume()
        bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        bus.unregister(this)
    }

    @Subscribe fun onLinkLoadError(e: LinkLoadError) {
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show()
    }

    @Subscribe fun onLinkNotExists(e: LinkNotExists) {
        adapter.header = null
        Toast.makeText(this, R.string.link_not_exists, Toast.LENGTH_LONG).show()
    }

    @Subscribe fun onCommentListLoadError(e: CommentListLoadError) {
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show()
    }

    @Subscribe fun onCommentListEmpty(e: CommentListEmpty) {
        adapter.removeItems()
    }

    companion object {
        val LINK_ID: String = "LINK_ID"
    }
}