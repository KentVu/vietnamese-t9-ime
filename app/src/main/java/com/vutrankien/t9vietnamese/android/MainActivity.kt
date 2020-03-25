package com.vutrankien.t9vietnamese.android

import android.app.Activity
import android.content.Context
import android.inputmethodservice.KeyboardView
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    private val logic: UiLogic = UiLogic.DefaultUiLogic()

    companion object {
        private const val WAKELOCK_TIMEOUT = 60000L
    }
    override val scope = CoroutineScope(Dispatchers.Main + Job())

    override val eventSource: Channel<EventWithData<Event, Key>> =
        Channel()

    override fun showProgress(bytes: Int) {
        displayInfo(R.string.engine_loading, bytes)
    }

    override fun showKeyboard() {
        log.w("View: TODO: showKeyboard")
        wakelock.run { if(isHeld) release() }
        displayInfo(R.string.notify_initialized)
        //defaultSharedPreferences.edit().putLong("load_time", loadTime).apply()
    }

    override fun showCandidates(candidates: Collection<String>) {
        log.d("View: showCandidates:$candidates")
        logic.updateCandidates(candidates)
    }

    override fun confirmInput(word: String) {
        log.d("View: confirmInput")
        // XXX Is inserting a space here a right place?
        findViewById<EditText>(R.id.editText).append(" $word")
        logic.clearCandidates()
    }

    private lateinit var wakelock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as T9Application).appComponent.inject(this)
        log = logFactory.newLog("MainActivity")
        setContentView(R.layout.main)
        val kbView = findViewById<KeyboardView>(R.id.dialpad)
        kbView.keyboard = T9Keyboard(this, R.xml.t9)
        kbView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSource
            )
        )
        logic.initializeCandidatesView(findViewById(R.id.candidates_view))
        //val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        //recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        //recyclerView.adapter = wordListAdapter
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK,
                "${BuildConfig.APPLICATION_ID}:MainActivity")

        wakelock.acquire(WAKELOCK_TIMEOUT)

        presenter.attachView(this)
        scope.launch {
            eventSource.send(Event.START.noData())
        }
    }

    private fun displayInfo(resId: Int, vararg formatArgs: Any) {
        val textView = findViewById<TextView>(R.id.text)
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        textView.text = getString(resId, *formatArgs)
    }

    override fun onStop() {
        super.onStop()
        log.d("onStop")
    }

    override fun onDestroy() {
        log.d("onDestroy")
        super.onDestroy()
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

    fun onCandidateClick(view: View) {
        log.d("onCandidateClick()")
        //engine.flush()
        (view as TextView).text = ""
    }

    fun onBtnClick(view: View) {
        val text = (view as Button).text
        val key = text[0]
        log.d("onBtnClick() btn=$key")
        scope.launch {
            eventSource.send(Event.KEY_PRESS.withData(Key.fromNum(key)))
        }
    }

    fun onBtnStarClick(view: View) {
        log.d("onBtnStarClick()")
    }
}
