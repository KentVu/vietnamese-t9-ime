package com.vutrankien.t9vietnamese

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import org.jetbrains.anko.find
import timber.log.Timber.d
import timber.log.Timber.i

class MainActivity : Activity() {
    lateinit var engine: T9Engine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        launch(UI) {
            i("Start initializing")
            engine = try {
                displayInfo(R.string.engine_loading)
                run(CommonPool) {
                    getEngineFor("vi-VN")
                }
            } catch (e: EnginePromise) {
                displayError(e)
                run(CommonPool) {
                    e.initializeThenGetBlocking()
                }
            }
            i("Initialization Completed!")
            displayInfo(R.string.notify_initialized)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        engine.close()
    }

    private fun displayInfo(resId: Int) {
        val textView = find<TextView>(R.id.text)
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        textView.text = getString(resId)
    }

    private fun displayError(e: Exception) {
        val textView: TextView = findViewById(R.id.text)
        val color = ContextCompat.getColor(this, android.R.color.holo_red_dark)
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
