package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.os.PowerManager
import android.view.inputmethod.InputConnection
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

abstract class AndroidView(
    val logFactory: LogFactory,
    protected val log: LogFactory.Log,
    protected val context: Context,
    override val scope: CoroutineScope,
    override val eventSource: ReceiveChannel<EventWithData<Event, Key>>,
    protected val eventSink: SendChannel<EventWithData<Event, Key>>,
    internal val inputConnection: InputConnection
) : View {

    private val preferences by lazy { Preferences(context.applicationContext) }
    protected val logic: UiLogic by lazy { UiLogic.DefaultUiLogic(preferences) }

    open fun init(kbView: KeyboardView, candidatesView: RecyclerView) {
        kbView.keyboard = T9Keyboard(context)
        kbView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSink
            )
        )
        logic.initializeCandidatesView(candidatesView)
        scope.launch {
            eventSink.send(Event.START.noData())
        }
    }

    abstract fun displayInfo(resId: Int, vararg formatArgs: Any)

    // TODO bytes -> percentage
    override fun showProgress(bytes: Int) {
        displayInfo(R.string.engine_loading, bytes)
    }

    override fun showCandidates(candidates: Collection<String>) {
        log.d("View: showCandidates:$candidates")
        logic.updateCandidates(candidates)
        //testingHook.onShowCandidates()
    }

    override fun showKeyboard() {
        log.w("View: TODO: showKeyboard")
        displayInfo(R.string.notify_initialized)
        //defaultSharedPreferences.edit().putLong("load_time", loadTime).apply()
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
}