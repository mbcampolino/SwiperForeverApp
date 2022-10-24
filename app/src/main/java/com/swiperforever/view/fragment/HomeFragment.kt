package com.swiperforever.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.swiperforever.R

class HomeFragment: Fragment() {

    private lateinit var btnStartSwipping : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutInflater.from(requireActivity()).inflate(R.layout.home_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnStartSwipping = view.findViewById(R.id.button_start_swipping)

        btnStartSwipping.setOnClickListener {
            //(activity as MainActivity).getShared().edit().putString(MainActivity.SHARED_SPEED_VALUE_NAME, txInput.text.toString()).apply()

            // start service
            //requireContext().stopService(Intent(requireContext(), SwipeForeverService::class.java))
        }

    }
}