package com.tomaszstankowski.wykopapi.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.webkit.WebView
import butterknife.bindView
import com.tomaszstankowski.wykopapi.R


class PreviewActivity : AppCompatActivity() {
    val webview: WebView by bindView(R.id.activity_preview_webview)
    val toolbar: Toolbar by bindView(R.id.activity_preview_toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val url = intent.getStringExtra(URL)
        webview.loadUrl(url)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.preview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    companion object {
        val URL = "URL"

        fun start(context: Context, url: String) {
            val intent = Intent(context, PreviewActivity::class.java)
            intent.putExtra(URL, url)
            context.startActivity(intent)
        }
    }

}