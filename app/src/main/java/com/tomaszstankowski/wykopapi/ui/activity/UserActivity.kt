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
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.tomaszstankowski.wykopapi.App
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.event.user.UserLoadError
import com.tomaszstankowski.wykopapi.event.user.UserNotFound
import com.tomaszstankowski.wykopapi.viemodel.UserViewModel
import com.tomaszstankowski.wykopapi.viemodel.UserViewModelFactory
import javax.inject.Inject
import javax.inject.Named


class UserActivity : AppCompatActivity(), LifecycleRegistryOwner {

    val registry = LifecycleRegistry(this)

    @Inject @field:[Named("user")] lateinit var bus: Bus
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

    override fun getLifecycle() = registry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).component.inject(this)

        setContentView(R.layout.activity_user)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = intent.getStringExtra(USERNAME)
        val factory = UserViewModelFactory(application, username)
        viewModel = ViewModelProviders.of(this, factory).get(UserViewModel::class.java)
        viewModel.user.observe(this, Observer { value ->
            if (value != null) {
                container.visibility = View.VISIBLE
                notFoundTv.visibility = View.GONE
                Glide.with(this)
                        .fromString()
                        .load(value.avatar)
                        .into(avatar)
                loginTv.text = value.login
                nameTv.text = value.name
                signupDateTv.text = getString(R.string.signup_date, value.signupDate)
                followersTv.text = getString(R.string.followers_count, value.followers)
                linksAddedTv.text = getString(R.string.links_added_count, value.linksAdded)
                aboutTv.text = value.about
                cityTv.text = value.city
                rankTv.text = if (value.rank > 0) getString(R.string.rank, value.rank) else ""
                val color = if (value.sex == "male") resources.getColor(R.color.blue)
                else resources.getColor(R.color.pink)
                rankTv.setBackgroundColor(color)
            }
        })
        viewModel.isLoading.observe(this, Observer { value ->
            if (value != null) {
                if (value)
                    progressbar.visibility = View.VISIBLE
                else
                    progressbar.visibility = View.GONE
            }
        })
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
        bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        bus.unregister(this)
    }

    @Subscribe fun userNotFound(e: UserNotFound) {
        notFoundTv.visibility = View.VISIBLE
        notFoundTv.text = getString(R.string.user_not_found, intent.getStringExtra(USERNAME))
        container.visibility = View.GONE
    }

    @Subscribe fun userLoadError(e: UserLoadError) {
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show()
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