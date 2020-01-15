package com.vutrankien.t9vietnamese

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.find
import timber.log.Timber.*
import java.util.concurrent.ForkJoinPool

class MainActivity : Activity() {

    companion object {
        private val WAKELOCK_TIMEOUT = 60000L
    }

    //private lateinit var engine: T9Engine
    //private lateinit var loadEngineDefer: Deferred<T9Engine>

    private lateinit var wakelock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.main)
        val recyclerView = find<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                BuildConfig.APPLICATION_ID)

        GlobalScope.launch(Dispatchers.Main) {
            i("Start initializing")
            val startTime = SystemClock.elapsedRealtime()
            val locale = "vi-VN"
            displayInfo(R.string.engine_loading)
            wakelock.acquire(WAKELOCK_TIMEOUT)
            //loadEngineDefer = async() {
            //    val trieDB = TrieDB(getFileStreamPath(VNConfiguration.dbname))
            //    if (!trieDB.initialized) {
            //        displayError("The engine is not initialized!")
            //        trieDB.readFrom(WordList.ViVNWordList(this@MainActivity))
            //    }
            //    T9Engine(locale, trieDB)
            //}
            //val pollDefer:PollDefer? =
            //    if (defaultSharedPreferences.contains("load_time")) PollDefer(loadEngineDefer,
            //            defaultSharedPreferences.getLong("load_time", -1), find(R.id
            //    .text))
            //    else null
            //engine = loadEngineDefer.await()

            wakelock.run { if(isHeld) release() }
            //pollDefer?.stop()
            val loadTime = (SystemClock.elapsedRealtime()
                    - startTime)
            i("Initialization Completed! loadTime=$loadTime")
            displayInfo(R.string.notify_initialized)
            defaultSharedPreferences.edit().putLong("load_time", loadTime).apply()
        }
    }

    override fun onStop() {
        super.onStop()
        d("onStop")
    }

    override fun onDestroy() {
        d("onDestroy")
        super.onDestroy()
        //try {
        //    GlobalScope.launch { run() { engine.close() } }
        //} catch (e: Exception) {
        //    w(e)
        //    displayError(e)
        //}
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
        //try {
        //    engine.input(text[0])
        //    val resultWords = engine.currentCandidates.take(10)
        //    find<TextView>(R.id.text).text = resultWords.joinToString()
        //    find<RecyclerView>(R.id.recycler_view).adapter = WordListAdapter(engine.currentCandidates.toList())
        //} catch (e: UninitializedPropertyAccessException) {
        //    w(e)
        //    displayError(e)
        //}
    }

    fun onCandidateClick(view: View) {
        d("onCandidateClick()")
        //engine.flush()
        (view as TextView).text = ""
    }

    fun onBtnStarClick(view: View) {
        d("onBtnStarClick()")
        //engine.flush()
        (view as TextView).text = ""
    }
}

class PollDefer(private val defer: Deferred<*>, private val time: Long, private val textView: TextView) {

    companion object {
        private val POLL_INTERVAL = 1000L
    }

    private val mTimer : CountDownTimer
    init {
        mTimer = object : CountDownTimer(time, POLL_INTERVAL) {
            override fun onFinish() {
                d("onFinish")
                if (!defer.isCompleted) {
                    w("engine still loading after $time ms")
                    textView.text = "engine still loading after $time ms"
                }
            }

            override fun onTick(millisUntilFinish: Long) {
                val percentDone = if (defer.isCompleted) 100 else (time - millisUntilFinish) * 100 / time
                d("onTick:$millisUntilFinish:$percentDone% done")
                if (defer.isCompleted) cancel() else
                    textView.text = "loading $percentDone%"
            }
        }
        mTimer.start()
    }
    fun stop() {
        mTimer.cancel()
    }
}
