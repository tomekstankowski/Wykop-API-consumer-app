package com.tomaszstankowski.wykopapi.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tomaszstankowski.wykopapi.R
import com.tomaszstankowski.wykopapi.ui.fragment.PromotedFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_frame_layout, PromotedFragment())
                    .commit()
        }
    }
}
