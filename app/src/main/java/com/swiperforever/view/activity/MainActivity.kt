package com.swiperforever.view.activity

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.swiperforever.R
import com.swiperforever.service.SwipeForeverService
import com.swiperforever.view.fragment.HomeFragment
import com.swiperforever.view.fragment.InstrunctionsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onStart() {
        super.onStart()

        val isServiceEnabled = isAccessibilityServiceEnabled(this, SwipeForeverService::class.java)
        val canDrawOverlay = Settings.canDrawOverlays(this)

        if (!isServiceEnabled || !canDrawOverlay) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root, InstrunctionsFragment(canDrawOverlay, isServiceEnabled))
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root, HomeFragment())
                .commit()
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>?): Boolean {
        val expectedComponentName = ComponentName(context, accessibilityService!!)
        val enabledServicesSetting =
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
                ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)
            if (enabledService != null && enabledService == expectedComponentName) return true
        }
        return false
    }
}