package com.swiperforever.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.SharedPreferences
import android.graphics.Path
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.swiperforever.R

class SwipeForeverService : AccessibilityService() {

    lateinit var linearLayout: LinearLayout

    lateinit var faster: Button
    lateinit var lower: Button
    lateinit var stopService: Button
    lateinit var txValue: TextView

    var active = false
    var userActive = false

    var speed = 1 // 1, 2, 3, 4

    override fun onServiceConnected() {
        super.onServiceConnected()
        active = true
        Log.v("MARCOS", "onServiceConnected")
        speed = getShared().getInt(SHARED_SPEED_VALUE_NAME, SHARED_SPEED_VALUE_DEFAULT)
        createControls()
        configureControls()
    }

    private fun getShared(): SharedPreferences = getSharedPreferences(SHARED_PREFERENCE_APPNAME, MODE_PRIVATE)

    private fun createControls() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        linearLayout = LinearLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.control_layout, linearLayout)
        wm.addView(linearLayout, lp)
    }

    private fun configureControls() {
        faster = linearLayout.findViewById(R.id.faster)
        lower= linearLayout.findViewById(R.id.lower)
        txValue = linearLayout.findViewById(R.id.tx_speed)
        stopService = linearLayout.findViewById(R.id.stop)

        txValue.text = speed.toString()

        faster.setOnClickListener {
            if (speed < 4) {
                speed += 1
            }

            getShared().edit().putInt(SHARED_SPEED_VALUE_NAME, speed).apply()
            txValue.text = speed.toString()
        }

        lower.setOnClickListener {
            if (speed > 1) {
                speed -= 1
            }

            getShared().edit().putInt(SHARED_SPEED_VALUE_NAME, speed).apply()
            txValue.text = speed.toString()
        }

        stopService.setOnClickListener {
            userActive =! userActive
            if (userActive) {
                stopService.text = "stop"
            } else {
                stopService.text = "start"
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        event?.source?.apply {

            if (active && userActive) {
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
                val stroke = StrokeDescription(dragRightPath, 0, (500 / speed).toLong())
                gestureBuilder.addStroke(stroke)
                dispatchGesture(gestureBuilder.build(), null, null)

                Log.v("MARCOS", "onAccessibilityEvent")

            }

            recycle()
        }
    }

    override fun onInterrupt() {
        Log.v("MARCOS", "onInterrupt")
        active = false
    }

    companion object {
        const val SHARED_PREFERENCE_APPNAME = "shared_preference_swipeforever"
        const val SHARED_SPEED_VALUE_NAME = "speed_value"
        const val SHARED_SPEED_VALUE_DEFAULT = 1
    }
}