package com.swiperforever.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.swiperforever.R

class ControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null) : CardView(context, attrs), View.OnClickListener {

    private lateinit var btnChangeSpeed: Button
    private lateinit var btnStartStopService: Button
    private lateinit var btnSettings: Button
    private lateinit var txCurrentCounter: TextView

    private var currentSpeed = 1
    private var activeService = false

    private var onControlUpdated: OnControlUpdated? = null

    init {
        inflate(context, R.layout.control_layout, this)
    }

    fun setOnControlUpdated(onControlUpdated: OnControlUpdated) {
        this.onControlUpdated = onControlUpdated
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        //if (onControlUpdated == null) return

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
        onControlUpdated?.onSpeedUpdate(currentSpeed)
    }

    override fun onClick(view: View?) {
        if (view == btnChangeSpeed) {

            if (currentSpeed < 3) {
                currentSpeed += 1
            } else {
                currentSpeed = 1
            }

            updateSpeedView()
        }

        if (view == btnStartStopService) {
            activeService =! activeService
            updateActiveView()
            onControlUpdated?.onStateRunning(activeService)
        }

        if (view == btnSettings) {
            onControlUpdated?.onSettingsOpen()
        }
    }

    private fun updateActiveView() {
        if (activeService) {
            btnStartStopService.setText(R.string.parar)
        } else {
            btnStartStopService.setText(R.string.iniciar)
        }
    }

    interface OnControlUpdated {
        fun onSpeedUpdate(speed: Int)
        fun onStateRunning(active: Boolean)
        fun onSettingsOpen()
    }
}