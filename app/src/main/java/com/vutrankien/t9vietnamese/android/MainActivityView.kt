package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.os.PowerManager
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.lib.Event
import com.vutrankien.t9vietnamese.lib.EventWithData
import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.LogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

class MainActivityView(
        val logFactory: LogFactory,
        context: Context,
        override val scope: CoroutineScope,
        channel: Channel<EventWithData<Event, Key>>,
        private val textView: TextView,
        internal val inputConnection: InputConnection
) : AndroidView(context, scope, channel) {

    companion object {
        private const val WAKELOCK_TIMEOUT = 60000L
    }

    private val log = logFactory.newLog("MainActivity.V")

    private val eventSink: SendChannel<EventWithData<Event, Key>> = channel

    private lateinit var wakelock: PowerManager.WakeLock

    fun init(kbView: KeyboardView, candidatesView: RecyclerView) {
        kbView.keyboard = T9Keyboard(context)
        kbView.setOnKeyboardActionListener(
                KeyboardActionListener(
                        logFactory,
                        scope,
                        eventSink
                )
        )
        logic.initializeCandidatesView(candidatesView)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK,
            "${BuildConfig.APPLICATION_ID}:MainActivity")

        wakelock.acquire(WAKELOCK_TIMEOUT)
        scope.launch {
            eventSink.send(Event.START.noData())
        }
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

    private fun displayInfo(resId: Int, vararg formatArgs: Any) {
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        textView.text = context.getString(resId, *formatArgs)
    }

    private fun displayError(msg: String) {
        val color = ContextCompat.getColor(context, android.R.color.holo_red_dark)
        textView.setTextColor(color)
        textView.text = context.getString(R.string.oops, msg)
    }

    private fun displayError(e: Exception) {
        displayError(e.message ?: "")
    }

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
        inputConnection.commitText(word, 1)
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
            get() = (this@MainActivityView.logic as UiLogic.DefaultUiLogic).wordListAdapter
        override val eventSink = this@MainActivityView.eventSink
        //override fun waitNewCandidates() {
        //    this@MainActivity.
        //}
    }

}