package com.hipark.randomer.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import com.hipark.randomer.R
import com.hipark.randomer.util.replaceFragmentInActivity
import com.hipark.randomer.util.setupActionBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.findFragmentById(R.id.contentFrame)
            as MainFragment? ?: MainFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
    }
}
