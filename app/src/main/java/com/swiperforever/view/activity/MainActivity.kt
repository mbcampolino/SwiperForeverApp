package com.swiperforever.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.swiperforever.R
import com.swiperforever.service.SwipeForeverService
import com.swiperforever.utill.SwipeForeverUtils
import com.swiperforever.view.fragment.HomeFragment
import com.swiperforever.view.fragment.InstructionsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onStart() {
        super.onStart()

        val isServiceEnabled = SwipeForeverUtils.isAccessibilityServiceEnabled(this, SwipeForeverService::class.java)

        if (!isServiceEnabled) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root, InstructionsFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root, HomeFragment())
                .commit()
        }
    }
}