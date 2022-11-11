package com.swiperforever.utill

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.text.TextUtils
import com.swiperforever.service.SwipeForeverService
import com.swiperforever.view.fragment.HomeFragment
import java.text.SimpleDateFormat
import java.util.*

object SwipeForeverUtils {

    const val SHARED_PREFERENCE_APPNAME = "shared_preference_swipeforever"
    const val SHARED_SPEED_VALUE_NAME = "speed_value"
    const val SHARED_SPEED_VALUE_DEFAULT = 1

    fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>?): Boolean {
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

    fun getShared(ctx: Context): SharedPreferences = ctx.getSharedPreferences(
        SHARED_PREFERENCE_APPNAME,
        AccessibilityService.MODE_PRIVATE
    )

    fun getCurrentHour(ctx: Context) : Int {
        val sdfHour = SimpleDateFormat(HomeFragment.FORMAT_LAST_HOUR, Locale.getDefault())
        val currentHourTime: String = sdfHour.format(Date())
        return getShared(ctx).getInt("likes_total_${currentHourTime}", 0)
    }
}