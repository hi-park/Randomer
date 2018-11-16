package com.hipark.randomer.addedititem

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hipark.randomer.R
import com.hipark.randomer.util.replaceFragmentInActivity

class AddEditItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_item)

        val itemId = intent.getStringExtra(AddEditItemFragment.ARGUMENT_EDIT_ITEM_ID)

        supportFragmentManager.findFragmentById(R.id.contentFrame) as
                AddEditItemFragment? ?: AddEditItemFragment.newInstance(itemId).also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
    }

    companion object {
        const val REQUEST_ADD_ITEM = 1
    }
}
