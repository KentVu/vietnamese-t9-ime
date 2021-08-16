package com.vutrankien.t9vietnamese.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.inputmethodservice.KeyboardView
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel


class MainActivity : Activity() {
    private val logFactory: LogFactory = AndroidLogFactory
    private val log = logFactory.newLog("MainActivity")
    lateinit var presenter: Presenter

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val channel: Channel<EventWithData<Event, Key>> = Channel()

    private val preferences by lazy { Preferences(applicationContext) }

    @VisibleForTesting
    lateinit var view: MainActivityView

    class MainActivityView(
        logFactory: LogFactory,
        context: Context,
        override val scope: CoroutineScope,
        channel: Channel<EventWithData<Event, Key>>,
        private val textView: TextView,
        override val inputConnection: InputConnection,
        private val txtSeq: TextView
    ) : AndroidView(
        logFactory, logFactory.newLog("MainActivity.V"),
        context, scope,
        channel, channel
    ) {

        companion object {
            private const val WAKELOCK_TIMEOUT = 60000L
        }

        private lateinit var wakelock: PowerManager.WakeLock

        override fun init(kbView: KeyboardView, candidatesView: RecyclerView) {
            super.init(kbView, candidatesView)
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakelock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK,
                "${BuildConfig.APPLICATION_ID}:MainActivity")

            wakelock.acquire(WAKELOCK_TIMEOUT)
        }

        fun onBtnClick(key: Char) {
            log.d("onBtnClick() key=$key")
            scope.launch {
                eventSink.send(Event.KEY_PRESS.withData(Key.fromChar(key)))
            }
        }

        fun onKeyDown(event: KeyEvent?) {
            if (event == null) {
                log.e("onKeyDown:event is null!")
                return
            }
            val char = event.unicodeChar.toChar()
            log.d("onKeyDown:num=$char")
            scope.launch {
                eventSink.send(Event.KEY_PRESS.withData(Key.fromChar(char)))
            }
        }

        override fun displayInfo(resId: Int, vararg formatArgs: Any) {
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            textView.text = context.getString(resId, *formatArgs)
        }

        override fun showKeyboard() {
            super.showKeyboard()
            wakelock.run { if(isHeld) release() }
        }

        override fun showCandidates(candidates: Collection<String>) {
            super.showCandidates(candidates)
            candidates.lastOrNull()?.let { txtSeq.text = it }
        }

        interface TestingHook {
            val candidatesAdapter: WordListAdapter
            val eventSink: SendChannel<EventWithData<Event, Key>>

            //fun waitNewCandidates()
        }

        /** For integration testing. */
        @VisibleForTesting
        val testingHook = object: TestingHook {
            override val candidatesAdapter: WordListAdapter
                //get() = this@MainActivity.findViewById<RecyclerView>(R.id.candidates_view).adapter as WordListAdapter
                get() = (this@MainActivityView).wordListAdapter
            override val eventSink = this@MainActivityView.eventSink
            //override fun waitNewCandidates() {
            //    this@MainActivity.
            //}
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        view = MainActivityView(
            logFactory,
            this,
            scope,
            channel,
            findViewById(R.id.text),
            findViewById<EditText>(R.id.editText).onCreateInputConnection(EditorInfo()),
            findViewById(R.id.dbg_seq)
        )
        presenter = Presenter(
            logFactory,
            DefaultT9Engine(
                DecomposedSeed(resources),
                VnPad,
                logFactory,
                TrieDb(logFactory, AndroidEnv(applicationContext))
            ),
            view
        )
        view.init(findViewById(R.id.dialpad), findViewById(R.id.candidates_view))

        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        log.d("onStop")
    }

    override fun onDestroy() {
        log.d("onDestroy")
        scope.cancel("MainActivity#onDestroy")
        super.onDestroy()
    }

    fun onCandidateClick(view: View) {
        log.d("onCandidateClick()")
        // TODO select
        (view as TextView).text = ""
    }

    fun onBtnClick(view: View) {
        val text = (view as Button).text
        val key = text[0]
        //log.d("onBtnClick() text=$text")
        this.view.onBtnClick(key)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        log.d("onKeyDown:$keyCode,$event")
        view.onKeyDown(event)
        return true
    }

    fun onBtnStarClick(view: View) {
        log.d("onBtnStarClick()")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val autoScroll = menu!!.findItem(R.id.auto_scroll_candidate)
        autoScroll.isChecked = preferences.autoScroll
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.enable_ime -> {
                startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0)
                return true
            }
            R.id.select_system_ime -> {
                val imeManager: InputMethodManager =
                    applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imeManager.showInputMethodPicker()
                return true
            }
            R.id.auto_scroll_candidate -> {
                item.isChecked = !item.isChecked
                preferences.autoScroll = item.isChecked
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log.d("onActivityResult:$requestCode:res=$resultCode:data=$data")
        super.onActivityResult(requestCode, resultCode, data)
    }
}
