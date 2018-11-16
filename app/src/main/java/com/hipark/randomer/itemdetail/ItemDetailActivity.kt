package com.hipark.randomer.itemdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hipark.randomer.R
import com.hipark.randomer.util.replaceFragmentInActivity

class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val itemId = intent.getStringExtra(EXTRA_ITEM_ID)

        supportFragmentManager.findFragmentById(R.id.contentFrame) as ItemDetailFragment? ?:
                ItemDetailFragment.newInstance(itemId).also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }
    }

    companion object {
        const val EXTRA_ITEM_ID = "ITEM_ID"
    }

}
