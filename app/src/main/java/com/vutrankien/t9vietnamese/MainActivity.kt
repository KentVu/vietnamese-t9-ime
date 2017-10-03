package com.vutrankien.t9vietnamese

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.find
import timber.log.Timber
import timber.log.Timber.d

class MainActivity : Activity() {
    lateinit var engine: T9Engine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        try {
            engine = getEngineFor("vi-VN")
        } catch (e: EnginePromise) {
            displayError(e)
            engine = e.initializeThenGetBlocking()
        }

    }

    private fun displayError(e: Exception) {
        // database not exists included?
        val textView = findViewById(R.id.text) as TextView
        val color: Int
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            color = getColor(android.R.color.holo_red_dark)
        } else {
            @Suppress("DEPRECATION")
            color = resources.getColor(android.R.color.holo_red_dark)
        }
        textView.setTextColor(color)
        textView.text = getString(R.string.oops, e
                .message)
    }

    fun onBtnClick(view: View) {
        val text = (view as Button).text
        d("onBtnClick() btn=" + text)
//        engine.input(text.substring(0..1))
        engine.input(text[0])
        find<TextView>(R.id.text).text = engine.currentCandidates.take(10).joinToString()
    }

    fun onCandidateClick(view: View) {
        d("onCandidateClick()")
        engine.flush()
        (view as TextView).text = ""
    }
}
