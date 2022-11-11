package com.swiperforever.view.components

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.swiperforever.R
import com.swiperforever.utill.SwipeForeverUtils.SHARED_PREFERENCE_APPNAME
import com.swiperforever.utill.SwipeForeverUtils.SHARED_SPEED_VALUE_NAME

@SuppressLint("SetTextI18n")
class ControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null) : CardView(context, attrs), View.OnClickListener {

    private lateinit var btnChangeSpeed: Button
    private lateinit var btnStartStopService: Button
    private lateinit var btnSettings: Button
    private lateinit var txCurrentCounter: TextView

    var currentSpeed = 1
    var activeService = false

    init {
        inflate(context, R.layout.control_layout, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        configureControls()
    }

    private fun configureControls() {
        btnChangeSpeed = findViewById(R.id.btn_change_speed)
        btnStartStopService = findViewById(R.id.stop)
        btnSettings = findViewById(R.id.config)

        txCurrentCounter = findViewById(R.id.tx_current_counter)

        btnChangeSpeed.setOnClickListener(this)
        btnStartStopService.setOnClickListener(this)
        btnSettings.setOnClickListener(this)
    }

    fun updateLikeCounter(likeCounter: Int) {
        txCurrentCounter.text = "$likeCounter"
    }

    private fun updateSpeedView() {
        btnChangeSpeed.text = "${currentSpeed}x"
    }

    override fun onClick(view: View?) {
        if (view == btnChangeSpeed) {

            if (currentSpeed < 3) {
                currentSpeed += 1
            } else {
                currentSpeed = 1
            }

            updateSpeedView()
            savePreferences(currentSpeed)
        }

        if (view == btnStartStopService) {
            activeService != activeService
            updateActiveView()
        }

        if (view == btnSettings) {
            // start stop
        }
    }

    private fun updateActiveView() {
        if (activeService) {
            btnStartStopService.setText(R.string.parar)
        } else {
            btnStartStopService.setText(R.string.iniciar)
        }
    }

    private fun savePreferences(speed: Int) {
        getShared().edit().putInt(SHARED_SPEED_VALUE_NAME, speed).apply()
    }

    private fun getShared(): SharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCE_APPNAME,
        AccessibilityService.MODE_PRIVATE
    )
}