package com.tomaszstankowski.wykopapi.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.ui.adapter.LinkListAdapter
import com.tomaszstankowski.wykopapi.viemodel.PromotedViewModel


/**
 * Displaying links from the promoted section.
 */
class PromotedFragment : LinkListFragment(), LinkListAdapter.OnClickListener {

    override fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(PromotedViewModel::class.java)
    }

    override fun inject() {
        (activity.application as App).component.inject(this)
    }
}