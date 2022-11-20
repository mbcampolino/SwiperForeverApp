package com.swiperforever.view.fragment

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.swiperforever.R
import com.swiperforever.utill.SwipeForeverUtils.getShared
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment: Fragment(), TextWatcher, View.OnClickListener {

    companion object {
        const val FORMAT_LAST_HOUR = "yyyy_MM_dd_HH"
        const val COUNTER_LIMIT = "counterLimit"
    }

    private lateinit var edValue : EditText
    private lateinit var progressValue : ProgressBar
    private lateinit var root : ConstraintLayout

    private lateinit var txLastHourLikes : TextView
    private lateinit var txTotalLikes : TextView
    private lateinit var txHourSave : TextView

    private lateinit var btnSavePreferences : Button
    private lateinit var btnShareApp : Button
    private lateinit var btnCopyPix : Button
    private lateinit var btnInfo : CardView
    private lateinit var cardAlertView : CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutInflater.from(requireActivity()).inflate(R.layout.home_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edValue = view.findViewById(R.id.ed_value_limit)
        progressValue = view.findViewById(R.id.progressBar)
        progressValue.min =  1

        txLastHourLikes = view.findViewById(R.id.tx_interactors_hour)
        txTotalLikes = view.findViewById(R.id.tx_interactors_total)
        txHourSave = view.findViewById(R.id.tx_interactors_pouped)

        btnCopyPix = view.findViewById(R.id.btn_copy_pix)
        btnShareApp = view.findViewById(R.id.btn_share_app)
        btnSavePreferences = view.findViewById(R.id.btn_save_preferences)
        btnInfo = view.findViewById(R.id.btn_info)
        cardAlertView = view.findViewById(R.id.alert_constraint)
        root = view.findViewById(R.id.root)

        // registra ultima hora
        val sdfHour = SimpleDateFormat(FORMAT_LAST_HOUR, Locale.getDefault())
        val currentHourTime: String = sdfHour.format(Date())
        txLastHourLikes.text = String.format(getString(R.string.interaoes_ultima_hora), getShared(view.context).getInt("likes_total_$currentHourTime", 0).toString())

        // registra likes totais
        txTotalLikes.text = String.format(getString(R.string.interaoes_totais), getShared(view.context).getInt("likes_total", 0).toString())

        edValue.setText(getShared(view.context).getInt(COUNTER_LIMIT, 500).toString())

        edValue.addTextChangedListener(this)
        btnSavePreferences.setOnClickListener(this)
        btnShareApp.setOnClickListener(this)
        btnCopyPix.setOnClickListener(this)
        btnInfo.setOnClickListener(this)
        cardAlertView.setOnClickListener(this)

        progressValue.progress = getShared(view.context).getInt("likes_total_$currentHourTime", 0)
        progressValue.max = getShared(view.context).getInt(COUNTER_LIMIT, 500)
    }

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        btnSavePreferences.isEnabled = getShared(requireContext()).getInt(COUNTER_LIMIT, 500).toString() != text
    }

    override fun onClick(view: View?) {
        if (view == btnSavePreferences) {
            val value = Integer.parseInt(edValue.text.toString())
            getShared(view.context).edit().putInt(COUNTER_LIMIT, value).apply()
            Toast.makeText(requireActivity(), getString(R.string.limite_alterado), Toast.LENGTH_LONG).show()
            btnSavePreferences.isEnabled = false
            progressValue.max = value
        } else if (view == btnShareApp) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.swiperforever")
            startActivity(intent)
        } else if (view == btnCopyPix) {
            val textToCopy = getString(R.string.chave_pix)
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(requireActivity(), getString(R.string.chaved_pix_copiada), Toast.LENGTH_LONG).show()
        } else if (view == btnInfo) {
            TransitionManager.beginDelayedTransition(root)
            if (cardAlertView.visibility == View.VISIBLE) {
                cardAlertView.visibility = View.GONE
            } else {
                cardAlertView.visibility = View.VISIBLE
            }
        } else if (view == cardAlertView) {
            TransitionManager.beginDelayedTransition(root)
            cardAlertView.visibility = View.GONE
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
}