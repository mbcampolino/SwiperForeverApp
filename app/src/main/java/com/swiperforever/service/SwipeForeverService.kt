package com.swiperforever.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.SharedPreferences
import android.graphics.Path
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import com.swiperforever.R
import com.swiperforever.utill.SwipeForeverUtils.SHARED_PREFERENCE_APPNAME
import com.swiperforever.utill.SwipeForeverUtils.SHARED_SPEED_VALUE_DEFAULT
import com.swiperforever.utill.SwipeForeverUtils.SHARED_SPEED_VALUE_NAME
import com.swiperforever.utill.SwipeForeverUtils.getCurrentHour
import com.swiperforever.utill.SwipeForeverUtils.getShared
import com.swiperforever.view.activity.MainActivity
import com.swiperforever.view.components.ControlView
import com.swiperforever.view.fragment.HomeFragment
import java.text.SimpleDateFormat
import java.util.*

class SwipeForeverService : AccessibilityService(), ControlView.OnControlUpdated {

    lateinit var linearLayout: LinearLayout
    private lateinit var controlView: ControlView
    lateinit var rootView: View

    var systemActive = false
    var userActive = false

    var counterLimit = 500
    var currentCounter = 0

    var speed = 1

    lateinit var wm: WindowManager

    override fun onServiceConnected() {
        Log.v("MARCOS", "onServiceConnected")
        super.onServiceConnected()

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        systemActive = true

        speed = getShared(this).getInt(SHARED_SPEED_VALUE_NAME, SHARED_SPEED_VALUE_DEFAULT)
        configLayoutOnStarted()
    }

    private fun configLayoutOnStarted() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        linearLayout = LinearLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.START and Gravity.TOP
        val inflater = LayoutInflater.from(this)
        rootView = inflater.inflate(R.layout.root_layout, linearLayout)
        wm.addView(linearLayout, lp)

        controlView = rootView.findViewById(R.id.control_view)
        controlView.setOnControlUpdated(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        event?.source?.apply {

            counterLimit = getShared(baseContext).getInt(HomeFragment.COUNTER_LIMIT, 500)

            currentCounter = getCurrentHour(baseContext)

            if (systemActive && userActive && currentCounter < counterLimit) {
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
        systemActive = false
    }

    override fun onSpeedUpdate(speed: Int) {
        this.speed = speed
    }

    override fun onStateRunning(active: Boolean) {
        userActive = active
    }

    override fun onSettingsOpen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        controlView.context.startActivity(intent)
    }

//    private fun savePreferences(speed: Int) {
//        getShared().edit().putInt(SHARED_SPEED_VALUE_NAME, speed).apply()
//    }
//
//    private fun getShared(): SharedPreferences = rootView.context.getSharedPreferences(
//        SHARED_PREFERENCE_APPNAME,
//        MODE_PRIVATE
//    )
}