package com.swiperforever.view.fragment

import android.accessibilityservice.AccessibilityService
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.swiperforever.R
import com.swiperforever.service.SwipeForeverService
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment: Fragment() {

    companion object {
        const val FORMAT_LAST_HOUR = "yyyy_MM_dd_HH"
        const val COUNTER_LIMIT = "counterLimit"
    }

    private lateinit var edValue : EditText
    private lateinit var progressValue : ProgressBar

    private lateinit var txLastHourLikes : TextView
    private lateinit var txTotalLikes : TextView
    private lateinit var txHourSave : TextView

    private lateinit var btnSavePreferences : Button
    private lateinit var btnShareApp : Button
    private lateinit var btnCopyPix : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutInflater.from(requireActivity()).inflate(R.layout.home_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edValue = view.findViewById(R.id.ed_value_limit)
        progressValue = view.findViewById(R.id.progressBar)

        txLastHourLikes = view.findViewById(R.id.tx_interactors_hour)
        txTotalLikes = view.findViewById(R.id.tx_interactors_total)
        txHourSave = view.findViewById(R.id.tx_interactors_pouped)

        btnCopyPix = view.findViewById(R.id.btn_copy_pix)
        btnShareApp = view.findViewById(R.id.btn_share_app)
        btnSavePreferences = view.findViewById(R.id.btn_save_preferences)

        // registra ultima hora
        val sdfHour = SimpleDateFormat(FORMAT_LAST_HOUR, Locale.getDefault())
        val currentHourTime: String = sdfHour.format(Date())
        txLastHourLikes.text = getShared().getInt("likes_total_$currentHourTime", 0).toString()

        // registra likes totais
        txTotalLikes.text = getShared().getInt("likes_total", 0).toString()

        edValue.setText(getShared().getInt(COUNTER_LIMIT, 500).toString())

        edValue.addTextChangedListener( object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btnSavePreferences.isEnabled = getShared().getInt(COUNTER_LIMIT, 500).toString() != text
            }
        })

        btnSavePreferences.setOnClickListener {
            val  value = Integer.parseInt(edValue.text.toString())
            getShared().edit().putInt(COUNTER_LIMIT, value).apply()
            Toast.makeText(requireActivity(), getString(R.string.limite_alterado), Toast.LENGTH_LONG).show()
            btnSavePreferences.isEnabled = false
        }

        btnShareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.swiperforever")
            startActivity(intent)
        }

        btnCopyPix.setOnClickListener {
            val textToCopy = getString(R.string.chave_pix)
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireActivity(), getString(R.string.chaved_pix_copiada), Toast.LENGTH_LONG).show()
        }
    }

    private fun getShared(): SharedPreferences = requireContext().getSharedPreferences(
        SwipeForeverService.SHARED_PREFERENCE_APPNAME,
        AccessibilityService.MODE_PRIVATE
    )
}