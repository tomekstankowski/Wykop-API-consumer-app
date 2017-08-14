package com.tomaszstankowski.wykopapi.ui.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.ui.activity.LinkActivity
import com.tomaszstankowski.wykopapi.ui.adapter.LinkListAdapter
import com.tomaszstankowski.wykopapi.viemodel.LinkListViewModel
import com.tomaszstankowski.wykopapi.viemodel.ResourceStatus


/**
 * Base class for other fragments displaying link previews in a recyclerview.
 * Does not know where links come from, it just knows how to display them.
 */
abstract class LinkListFragment : LifecycleFragment(), LinkListAdapter.OnClickListener {

    protected lateinit var viewModel: LinkListViewModel

    private val recyclerView: RecyclerView by bindView(R.id.fragment_links_recycler_view)
    private val refreshButton: Button by bindView(R.id.fragment_links_refresh_button)
    private val emptyView: TextView by bindView(R.id.fragment_links_empty_tv)
    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.fragment_links_swipe_refresh_layout)

    private lateinit var adapter: LinkListAdapter

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_links, container, false)
    }

    abstract fun setViewModel()

    abstract fun inject()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inject()
        setRecyclerview()
        setViewModel()
        observeData()
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
        refreshButton.setOnClickListener {
            viewModel.refresh()
            recyclerView.smoothScrollToPosition(0)
            refreshButton.visibility = View.GONE
        }
    }

    private fun setRecyclerview() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = LinkListAdapter(context, R.layout.link_preview)
        adapter.emptyView = emptyView
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(ScrollListener(layoutManager))
    }

    private fun observeData() {
        viewModel.links.observe(this, Observer { if (it != null) adapter.setItems(it) })
        viewModel.linksStatus.observe(this, Observer {
            swipeRefreshLayout.isRefreshing = it == ResourceStatus.LOADING
            if (it == ResourceStatus.ERROR)
                Toast.makeText(activity, R.string.load_error, Toast.LENGTH_LONG).show()
        })
    }

    override fun onResume() {
        super.onResume()
        adapter.onClickListener = this
    }

    override fun onPause() {
        super.onPause()
        adapter.onClickListener = null
    }

    override fun onItemClicked(position: Int, link: Link) {
        LinkActivity.start(activity, link.id)
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

    inner open class ScrollListener(val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
            val isLoading = viewModel.linksStatus.value == ResourceStatus.LOADING
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
                    viewModel.loadNextPage()
            }
        }
    }
}