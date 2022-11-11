package com.swiperforever.view.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.swiperforever.R

class PointView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null) : CardView(context, attrs) {

    init {
        inflate(context, R.layout.point_layout, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // do something with views
    }
}