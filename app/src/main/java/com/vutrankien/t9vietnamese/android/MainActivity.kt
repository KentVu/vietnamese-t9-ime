package com.vutrankien.t9vietnamese.android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import com.vutrankien.t9vietnamese.lib.View as MVPView

class MainActivity : Activity(), MVPView {
    @Inject
    lateinit var logFactory: LogFactory
    private lateinit var log: LogFactory.Log
    @Inject
    lateinit var presenter: Presenter

    companion object {
        private const val WAKELOCK_TIMEOUT = 60000L
    }
    override val scope = CoroutineScope(Dispatchers.Main + Job())

    override val eventSource: Channel<EventWithData<Event, Key>> =
        Channel()

    override fun showProgress() {
        displayInfo(R.string.engine_loading)
    }

    override fun showKeyboard() {
        log.w("View: TODO: showKeyboard")
        wakelock.run { if(isHeld) release() }
        displayInfo(R.string.notify_initialized)
        //defaultSharedPreferences.edit().putLong("load_time", loadTime).apply()
    }

    override fun showCandidates(cand: Set<String>) {
        log.d("View: showCandidates:$cand")
        wordListAdapter.update(cand)
    }

    override fun confirmInput(word: String) {
        log.d("View: confirmInput")
        // XXX Is inserting a space here a right place?
        findViewById<EditText>(R.id.editText).append(" $word")
        wordListAdapter.clear()
    }

    //private lateinit var engine: T9Engine
    //private lateinit var loadEngineDefer: Deferred<T9Engine>

    private lateinit var wakelock: PowerManager.WakeLock

    private val wordListAdapter = WordListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as T9Application).appComponent.inject(this)
        log = logFactory.newLog("MainActivity")
        setContentView(R.layout.main)
        presenter.attachView(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = wordListAdapter
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK,
                "${BuildConfig.APPLICATION_ID}:MainActivity")

        wakelock.acquire(WAKELOCK_TIMEOUT)
        scope.launch {
            eventSource.send(Event.START.noData())
        }
    }

    override fun onStop() {
        super.onStop()
        log.d("onStop")
    }

    override fun onDestroy() {
        log.d("onDestroy")
        super.onDestroy()
        //try {
        //    GlobalScope.launch { run() { engine.close() } }
        //} catch (e: Exception) {
        //    w(e)
        //    displayError(e)
        //}
    }

    private fun displayInfo(resId: Int) {
        val textView = findViewById<TextView>(R.id.text)
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
        val key = text[0]
        log.d("onBtnClick() btn=$key")
        scope.launch {
            eventSource.send(Event.KEY_PRESS.withData(Key.fromNum(key)))
        }
    }

    fun onCandidateClick(view: View) {
        log.d("onCandidateClick()")
        //engine.flush()
        (view as TextView).text = ""
    }

    fun onBtnStarClick(view: View) {
        log.d("onBtnStarClick()")
    }
}

class PollDefer(private val defer: Deferred<*>, private val time: Long, private val textView: TextView) {
    private lateinit var log: LogFactory.Log

    companion object {
        private val POLL_INTERVAL = 1000L
    }

    private val mTimer : CountDownTimer
    init {
        mTimer = object : CountDownTimer(time, POLL_INTERVAL) {
            override fun onFinish() {
                log.d("onFinish")
                if (!defer.isCompleted) {
                    log.w("engine still loading after $time ms")
                    textView.text = "engine still loading after $time ms"
                }
            }

            override fun onTick(millisUntilFinish: Long) {
                val percentDone = if (defer.isCompleted) 100 else (time - millisUntilFinish) * 100 / time
                log.d("onTick:$millisUntilFinish:$percentDone% done")
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
