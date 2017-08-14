package com.tomaszstankowski.wykopapi.ui.activity

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.bumptech.glide.Glide
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.model.User
import com.tomaszstankowski.wykopapi.viemodel.ResourceStatus
import com.tomaszstankowski.wykopapi.viemodel.UserViewModel
import com.tomaszstankowski.wykopapi.viemodel.UserViewModelFactory

/**
 * Displays details about user.
 */
class UserActivity : AppCompatActivity(), LifecycleRegistryOwner {

    val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry

    lateinit var viewModel: UserViewModel

    val toolbar: Toolbar by bindView(R.id.activity_user_toolbar)
    val progressbar: ProgressBar by bindView(R.id.activity_user_progressbar)
    val container: View by bindView(R.id.activity_user_container)
    val notFoundTv: TextView by bindView(R.id.activity_user_not_found_tv)
    val loginTv: TextView by bindView(R.id.activity_user_login_tv)
    val signupDateTv: TextView by bindView(R.id.activity_user_signup_date_tv)
    val followersTv: TextView by bindView(R.id.activity_user_followers_tv)
    val linksAddedTv: TextView by bindView(R.id.activity_user_links_added_tv)
    val rankTv: TextView by bindView(R.id.activity_user_rank_tv)
    val nameTv: TextView by bindView(R.id.activity_user_name_tv)
    val aboutTv: TextView by bindView(R.id.activity_user_about_tv)
    val cityTv: TextView by bindView(R.id.activity_user_city_tv)
    val avatar: ImageView by bindView(R.id.activity_user_avatar_iv)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).component.inject(this)
        setContentView(R.layout.activity_user)
        setActionBar()
        setViewModel()
    }

    private fun setActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setViewModel() {
        val username = intent.getStringExtra(USERNAME)
        val factory = UserViewModelFactory(application, username)
        viewModel = ViewModelProviders.of(this, factory).get(UserViewModel::class.java)
        viewModel.user.observe(this, Observer { displayUser(it) })
        viewModel.userStatus.observe(this, Observer {
            progressbar.visibility = if (it == ResourceStatus.LOADING) View.VISIBLE else View.GONE
            when (it) {
                ResourceStatus.ERROR ->
                    Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show()
                ResourceStatus.NOT_FOUND -> {
                    notFoundTv.visibility = View.VISIBLE
                    notFoundTv.text = getString(R.string.user_not_found, intent.getStringExtra(USERNAME))
                    container.visibility = View.GONE
                }
            }
        })
    }

    private fun displayUser(user: User?) {
        if (user != null) {
            container.visibility = View.VISIBLE
            notFoundTv.visibility = View.GONE
            Glide.with(this)
                    .fromString()
                    .load(user.avatar)
                    .into(avatar)
            loginTv.text = user.login
            nameTv.text = user.name
            signupDateTv.text = getString(R.string.signup_date, user.signupDate)
            followersTv.text = getString(R.string.followers_count, user.followers)
            linksAddedTv.text = getString(R.string.links_added_count, user.linksAdded)
            aboutTv.text = user.about
            cityTv.text = user.city
            rankTv.text = if (user.rank > 0) getString(R.string.rank, user.rank) else ""
            val color = if (user.sex == "male") resources.getColor(R.color.blue)
            else resources.getColor(R.color.pink)
            rankTv.setBackgroundColor(color)
        } else {
            container.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        val USERNAME = "USERNAME"

        fun start(context: Context, username: String) {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(UserActivity.USERNAME, username)
            context.startActivity(intent)
        }
    }
}