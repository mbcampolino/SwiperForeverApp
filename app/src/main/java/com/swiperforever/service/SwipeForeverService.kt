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
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.swiperforever.R
import com.swiperforever.view.fragment.HomeFragment
import java.text.SimpleDateFormat
import java.util.*

class SwipeForeverService : AccessibilityService(), View.OnClickListener {

    companion object {
        const val SHARED_PREFERENCE_APPNAME = "shared_preference_swipeforever"
        const val SHARED_SPEED_VALUE_NAME = "speed_value"
        const val SHARED_SPEED_VALUE_DEFAULT = 1
    }

    lateinit var linearLayout: LinearLayout
    lateinit var faster: Button
    lateinit var lower: Button
    lateinit var stopService: Button
    lateinit var txValue: TextView
    lateinit var txCurrentCounter: TextView

    var active = false
    var userActive = false

    var counterLimit = 500
    var currentCounter = 0

    var speed = 1

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
        txCurrentCounter = linearLayout.findViewById(R.id.tx_current_counter)

        txValue.text = speed.toString()

        faster.setOnClickListener(this)
        lower.setOnClickListener(this)
        stopService.setOnClickListener(this)
    }

    private fun getCurrentHour() : Int {
        val sdfHour = SimpleDateFormat(HomeFragment.FORMAT_LAST_HOUR, Locale.getDefault())
        val currentHourTime: String = sdfHour.format(Date())
        return getShared().getInt("likes_total_${currentHourTime}", 0)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        event?.source?.apply {

            counterLimit = getShared().getInt(HomeFragment.COUNTER_LIMIT, 500)

            currentCounter = getCurrentHour()

            if (active && userActive && currentCounter < counterLimit) {
                val displayMetrics = resources.displayMetrics
                val centerWidth = displayMetrics.widthPixels / 2
                val centerHeight = displayMetrics.heightPixels / 2

                val dragRightPath = Path().apply {

                    val randomCenter = (centerHeight / 4..centerHeight / 2).random()
                    moveTo(randomCenter.toFloat(), randomCenter.toFloat())

                    val rndsSwipeDistance = displayMetrics.widthPixels//(centerWidth / 4..centerWidth / 2).random()
                    lineTo(centerWidth.toFloat() + rndsSwipeDistance, randomCenter.toFloat())
                    Log.v("MARCOS", "from $centerWidth to ${centerWidth + rndsSwipeDistance}")
                }

                val gestureBuilder =  GestureDescription.Builder()
                val stroke = StrokeDescription(dragRightPath, 0, (500 / speed).toLong())
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

        var total = getShared().getInt("likes_total", 0)
        total += 1
        getShared().edit().putInt("likes_total", total).apply()

        var totalLastHour = getCurrentHour()
        totalLastHour += 1
        getShared().edit().putInt("likes_total_$currentHourTime", totalLastHour).apply()

        txCurrentCounter.text = totalLastHour.toString()
    }

    override fun onInterrupt() {
        Log.v("MARCOS", "onInterrupt")
        active = false
    }

    override fun onClick(view: View?) {
        if (view == faster) {
            if (speed < 4) {
                speed += 1
            }

            savePreferences(speed)
        } else if (view == lower) {
            if (speed > 1) {
                speed -= 1
            }

            savePreferences(speed)
        } else if (view == stopService) {
            userActive =! userActive
            if (userActive) {
                stopService.text = getString(R.string.parar)
                txCurrentCounter.text = currentCounter.toString()
            } else {
                stopService.text = getString(R.string.iniciar)
            }
        }
    }

    private fun savePreferences(speed: Int) {
        getShared().edit().putInt(SHARED_SPEED_VALUE_NAME, speed).apply()
        txValue.text = speed.toString()
    }
}