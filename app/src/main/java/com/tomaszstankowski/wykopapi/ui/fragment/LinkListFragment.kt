package com.tomaszstankowski.wykopapi.ui.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.event.link_list.LinkListEmpty
import com.tomaszstankowski.wykopapi.event.link_list.LinkListLoadError
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.ui.activity.LinkActivity
import com.tomaszstankowski.wykopapi.ui.adapter.LinkListAdapter
import com.tomaszstankowski.wykopapi.viemodel.LinkListViewModel
import javax.inject.Inject
import javax.inject.Named


/**
 * Base class for other fragments displaying links in a recycler view.
 * Does not know where links come from, it just knows how to display them.
 */
abstract class LinkListFragment : LifecycleFragment(), LinkListAdapter.OnClickListener {

    private val recyclerView: RecyclerView by bindView(R.id.fragment_links_recycler_view)
    private val progressbar: ProgressBar by bindView(R.id.fragment_links_progressbar)
    private val refreshButton: Button by bindView(R.id.fragment_links_refresh_button)
    private val emptyView: TextView by bindView(R.id.fragment_links_empty_tv)
    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.fragment_links_swipe_refresh_layout)
    private lateinit var adapter: LinkListAdapter
    protected lateinit var viewModel: LinkListViewModel
    @Inject @field:[Named("link_list")] protected lateinit var bus: Bus


    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_links, container, false)
    }


    abstract fun initViewModel()

    abstract fun inject()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        inject()

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = LinkListAdapter(context, R.layout.link_preview)
        adapter.emptyView = emptyView
        adapter.onClickListener = this
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(ScrollListener(layoutManager))

        initViewModel()
        viewModel.links.observe(this, Observer {
            if (it != null)
                adapter.setItems(it)
        })
        viewModel.isLoading.observe(this, Observer {
            if (it != null) {
                if (it == true)
                    progressbar.visibility = View.VISIBLE
                else {
                    progressbar.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        refreshButton.setOnClickListener {
            viewModel.refresh()
            recyclerView.smoothScrollToPosition(0)
            refreshButton.visibility = View.GONE
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

    @Subscribe fun onListEmpty(e: LinkListEmpty) {
        adapter.removeItems()
    }

    @Subscribe fun onLoadError(e: LinkListLoadError) {
        Toast.makeText(context, R.string.load_error, Toast.LENGTH_LONG).show()
    }

    override fun onItemClicked(position: Int, link: Link) {
        val intent = Intent(context, LinkActivity::class.java)
        intent.putExtra(LinkActivity.LINK_ID, link.id)
        startActivity(intent)
    }

    fun showRefreshButton() {
        refreshButton.visibility = View.VISIBLE
        val scaleY = AnimatorInflater.loadAnimator(activity, R.animator.scale_y_up)
        val scaleX = AnimatorInflater.loadAnimator(activity, R.animator.scale_x_up)
        val set = AnimatorSet()
        set.playTogether(scaleY, scaleX)
        set.setTarget(refreshButton)
        set.start()
    }

    fun loadNextPage() {
        viewModel.loadNextPage()
    }

    inner open class ScrollListener(val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
            val isLoading = viewModel.isLoading.value ?: false
            if (dy < 0) {
                val isTop = pastVisibleItems < 5
                if (!isTop && !isLoading)
                    showRefreshButton()
            }
            if (dy > 0) {
                refreshButton.visibility = View.GONE
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val isBottom = pastVisibleItems + visibleItemCount >= totalItemCount
                if (isBottom && !isLoading && viewModel.hasMorePages)
                    loadNextPage()
            }
        }
    }
}