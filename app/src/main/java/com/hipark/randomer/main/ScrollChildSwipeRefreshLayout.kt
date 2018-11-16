package com.hipark.randomer.main

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {

    var scrollUpChild : View? = null

    override fun canChildScrollUp() =
        scrollUpChild?.canScrollHorizontally(-1) ?: super.canChildScrollUp()



}