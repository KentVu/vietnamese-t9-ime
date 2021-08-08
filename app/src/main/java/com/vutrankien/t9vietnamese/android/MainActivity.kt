package com.vutrankien.t9vietnamese.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.inputmethodservice.KeyboardView
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.Editable
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
import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import com.vutrankien.t9vietnamese.lib.View as MVPView


class MainActivity : Activity(), MVPView {
    private val logFactory: LogFactory = AndroidLogFactory
    private val log = logFactory.newLog("MainActivity")
    lateinit var presenter: Presenter

    companion object {
        private const val WAKELOCK_TIMEOUT = 60000L
    }

    override val scope = CoroutineScope(Dispatchers.Main + Job())

    private val channel: Channel<EventWithData<Event, Key>> = Channel()
    override val eventSource: ReceiveChannel<EventWithData<Event, Key>> = channel
    private val eventSink: SendChannel<EventWithData<Event, Key>> = channel

    private val preferences by lazy { Preferences(applicationContext) }

    private val logic: UiLogic by lazy { UiLogic.DefaultUiLogic(preferences) }

    // TODO bytes -> percentage
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
        //testingHook.onShowCandidates()
    }

    override fun candidateSelected(selectedCandidate: Int) {
        logic.selectCandidate(selectedCandidate)
    }

    override fun confirmInput(word: String) {
        log.d("View: confirmInput($word)")
        // XXX Is inserting a space here a right place?
        findViewById<EditText>(R.id.editText).apply {
            if (length() > 0)
                append(" ")
            append(word)
        }
        logic.clearCandidates()
    }

    override fun deleteBackward() {
        log.d("deleteBackward")
        inputConnection.deleteSurroundingText(1, 0)
//        findViewById<MyEditText>(R.id.editText).apply {
//            inputConnection!!.deleteSurroundingText(1, 0)
//        }
//        findViewById<EditText>(R.id.editText).onCreateInputConnection()
    }

    private lateinit var wakelock: PowerManager.WakeLock
    internal lateinit var inputConnection: InputConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = Presenter(
            logFactory,
            DefaultT9Engine(
                DecomposedSeed(resources),
                VnPad,
                logFactory,
                TrieDb(logFactory, AndroidEnv(applicationContext))
            )
        )
        setContentView(R.layout.main)
        val kbView = findViewById<KeyboardView>(R.id.dialpad)
        kbView.keyboard = T9Keyboard(this)
        kbView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSink
            )
        )
        logic.initializeCandidatesView(findViewById(R.id.candidates_view))
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK,
                "${BuildConfig.APPLICATION_ID}:MainActivity")

        wakelock.acquire(WAKELOCK_TIMEOUT)

        inputConnection = findViewById<EditText>(R.id.editText).onCreateInputConnection(EditorInfo())

        presenter.attachView(this)
        scope.launch {
            eventSink.send(Event.START.noData())
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
        // TODO select
        (view as TextView).text = ""
    }

    fun onBtnClick(view: View) {
        val text = (view as Button).text
        val key = text[0]
        log.d("onBtnClick() btn=$key")
        scope.launch {
            eventSink.send(Event.KEY_PRESS.withData(Key.fromChar(key)))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) {
            log.e("onKeyDown:event is null!")
            return false
        }
        val char = event.unicodeChar.toChar()
        log.d("onKeyDown:$keyCode,$event,num=$char")
        scope.launch {
            eventSink.send(Event.KEY_PRESS.withData(Key.fromChar(char)))
        }
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
                    applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
            get() = (this@MainActivity.logic as UiLogic.DefaultUiLogic).wordListAdapter
        override val eventSink = this@MainActivity.eventSink
        //override fun waitNewCandidates() {
        //    this@MainActivity.
        //}
    }
}
