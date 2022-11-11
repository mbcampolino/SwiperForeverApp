package com.swiperforever.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.RelativeLayout
import com.swiperforever.R
import com.swiperforever.utill.SwipeForeverUtils.SHARED_SPEED_VALUE_DEFAULT
import com.swiperforever.utill.SwipeForeverUtils.SHARED_SPEED_VALUE_NAME
import com.swiperforever.utill.SwipeForeverUtils.getCurrentHour
import com.swiperforever.utill.SwipeForeverUtils.getShared
import com.swiperforever.view.components.ControlView
import com.swiperforever.view.components.PointView
import com.swiperforever.view.fragment.HomeFragment
import java.text.SimpleDateFormat
import java.util.*

class SwipeForeverService : AccessibilityService() {

    lateinit var viewGroupRoot: RelativeLayout

    private lateinit var controlView: ControlView
    private lateinit var pointView: PointView

    lateinit var viewToTap: View

    var active = false
    var userActive = false

    var counterLimit = 500
    var currentCounter = 0

    var speed = 1

    lateinit var wm: WindowManager

    override fun onServiceConnected() {
        Log.v("MARCOS", "onServiceConnected")
        super.onServiceConnected()

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        viewGroupRoot = RelativeLayout(this)
        active = true

        speed = getShared(this).getInt(SHARED_SPEED_VALUE_NAME, SHARED_SPEED_VALUE_DEFAULT)
        configLayoutOnStarted()
    }

    private fun configLayoutOnStarted() {
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.START
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.root_layout, viewGroupRoot)
        wm.addView(ControlView(this), lp)
    }

//    private fun showPointPosition() {
//        //wm.removeView(headerControl)
//        val lp = WindowManager.LayoutParams()
//        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
//        lp.format = PixelFormat.TRANSLUCENT
//        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT
//        lp.gravity = Gravity.TOP
//        val inflater = LayoutInflater.from(this)
//        inflater.inflate(R.layout.control_layout, headerControl)
//        wm.addView(headerControl, lp)
//    }

//    private fun createDragDropSystem() {
//
//        //viewToTap = headerControl.findViewById(R.id.view_to_tap_start)
//        viewToTap.isClickable = true
//        viewToTap.isFocusable = true
//
//        viewToTap.setOnDragListener { v, event ->
//            // params = v.layoutParams as RelativeLayout.LayoutParams
//            //params.topMargin = event.x.toInt()
//            //params.bottomMargin = event.y.toInt()
//
//            //params.marginStart = event.x.toInt() - (viewToTap.width.div(2))
//            //params.topMargin = event.y.toInt() - (viewToTap.height.div(2))
//
//            viewToTap.x = event.x - viewToTap.width.div(2)
//            viewToTap.y = event.y - viewToTap.height.div(2)
//
//            //v.layoutParams = params
//            false
//        }
//    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        event?.source?.apply {

            counterLimit = getShared(baseContext).getInt(HomeFragment.COUNTER_LIMIT, 500)

            currentCounter = getCurrentHour(baseContext)

            if (active && userActive && currentCounter < counterLimit) {
                val displayMetrics = resources.displayMetrics
                val centerWidth = displayMetrics.widthPixels / 2
                val centerHeight = displayMetrics.heightPixels / 2

                val dragRightPath = Path().apply {

                    val randomCenter = (centerHeight / 4..centerHeight / 2).random()
                    moveTo(randomCenter.toFloat(), randomCenter.toFloat())

                    val rndsSwipeDistance = (centerWidth / 4..centerWidth / 2).random()
                    lineTo(centerWidth.toFloat() + rndsSwipeDistance, randomCenter.toFloat())
                    Log.v("MARCOS", "from $centerWidth to ${centerWidth + rndsSwipeDistance}")
                }

                val gestureBuilder =  GestureDescription.Builder()
                val stroke =
                    GestureDescription.StrokeDescription(dragRightPath, 0, (500 / speed).toLong())
                gestureBuilder.addStroke(stroke)
                dispatchGesture(gestureBuilder.build(), registerCallback, null)

                Log.v("MARCOS", "onAccessibilityEvent")
            }
            recycle()
        }
    }

    private val registerCallback = object : GestureResultCallback() {

        override fun onCancelled(gestureDescription: GestureDescription?) {
            super.onCancelled(gestureDescription)
            Log.v("MARCOS", "onCancelled")
            registerLikes()
        }

        override fun onCompleted(gestureDescription: GestureDescription?) {
            super.onCompleted(gestureDescription)
            Log.v("MARCOS", "onCompleted")
        }

    }

    fun registerLikes() {

        val sdfHour = SimpleDateFormat(HomeFragment.FORMAT_LAST_HOUR, Locale.getDefault())
        val currentHourTime: String = sdfHour.format(Date())

        var total = getShared(this).getInt("likes_total", 0)
        total += 1
        getShared(this).edit().putInt("likes_total", total).apply()

        var totalLastHour = getCurrentHour(this)
        totalLastHour += 1
        getShared(this).edit().putInt("likes_total_$currentHourTime", totalLastHour).apply()

        controlView.updateLikeCounter(totalLastHour)
    }

    override fun onInterrupt() {
        Log.v("MARCOS", "onInterrupt")
        active = false
    }
}