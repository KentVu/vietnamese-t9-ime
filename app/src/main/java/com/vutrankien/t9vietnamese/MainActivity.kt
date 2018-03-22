package com.vutrankien.t9vietnamese

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.find
import timber.log.Timber.*
import java.util.concurrent.ForkJoinPool

class MainActivity : Activity() {
    private lateinit var engine: T9Engine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val recyclerView = find<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        launch(UI) {
            i("Start initializing")
            val startTime = SystemClock.elapsedRealtime()
            val locale = "vi-VN"
            val trieDB = TrieDB(getFileStreamPath(VNConfiguration.dbname))
            displayInfo(R.string.engine_loading)
            if (!trieDB.initialized) {
                displayError("The engine is not initialized!")
                run(CommonPool) {
                    trieDB.readFrom(Wordlist.ViVNWordList(this@MainActivity))
                }
            }
            engine = T9Engine(locale, trieDB)

            val loadTime = (SystemClock.elapsedRealtime()
                    - startTime)
            i("Initialization Completed! loadTime=$loadTime")
            displayInfo(R.string.notify_initialized)
            defaultSharedPreferences.edit().putLong("load_time", loadTime).apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            launch { run(CommonPool) { engine.close() } }
        } catch (e: Exception) {
            w(e)
            displayError(e)
        }
    }

    private fun displayInfo(resId: Int) {
        val textView = find<TextView>(R.id.text)
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        textView.text = getString(resId)
    }

    private fun displayError(msg: String) {
        val textView: TextView = findViewById(R.id.text)
        val color = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        textView.setTextColor(color)
        textView.text = getString(R.string.oops, msg)
    }

    private fun displayError(e: Exception) {
        displayError(e.message ?: "")
    }

    fun onBtnClick(view: View) {
        val text = (view as Button).text
        d("onBtnClick() btn=" + text.substring(0..1))
        try {
            engine.input(text[0])
            val resultWords = engine.currentCandidates.take(10)
            find<TextView>(R.id.text).text = resultWords.joinToString()
            find<RecyclerView>(R.id.recycler_view).adapter = WordListAdapter(resultWords)
        } catch (e: UninitializedPropertyAccessException) {
            w(e)
            displayError(e)
        }
    }

    fun onCandidateClick(view: View) {
        d("onCandidateClick()")
        engine.flush()
        (view as TextView).text = ""
    }

    fun onBtnStarClick(view: View) {
        d("onBtnStarClick()")
        engine.flush()
        (view as TextView).text = ""
    }
}
