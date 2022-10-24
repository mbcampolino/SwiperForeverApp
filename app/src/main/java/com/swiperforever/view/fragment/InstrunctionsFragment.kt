package com.swiperforever.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.swiperforever.R

class InstrunctionsFragment(private val canDrawOverlay: Boolean, private val isServiceEnabled: Boolean): Fragment() {

    lateinit var btnEnableServices : Button
    lateinit var btnDisplayOverApps : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutInflater.from(requireActivity()).inflate(R.layout.instrunctions_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDisplayOverApps = view.findViewById(R.id.button_enable_display)
        btnEnableServices = view.findViewById(R.id.button_enable_service)

        btnEnableServices.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnDisplayOverApps.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity?.packageName)
            )
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            checkButtonStates()
        }, 500)
    }

    private fun checkButtonStates() {
        btnDisplayOverApps.visibility = if (canDrawOverlay) View.GONE else View.VISIBLE
        btnEnableServices.visibility = if (isServiceEnabled) View.GONE else View.VISIBLE
    }
}