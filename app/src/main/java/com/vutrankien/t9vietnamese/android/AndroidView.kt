package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.os.PowerManager
import android.view.inputmethod.InputConnection
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

abstract class AndroidView(
    private val logFactory: LogFactory,
    protected val log: LogFactory.Log,
    protected val context: Context,
    override val scope: CoroutineScope,
    override val eventSource: ReceiveChannel<EventWithData<Event, Key>>,
    protected val eventSink: SendChannel<EventWithData<Event, Key>>,
) : View {

    internal abstract val inputConnection: InputConnection
    private val preferences by lazy { Preferences(context.applicationContext) }
    private lateinit var candidatesView: RecyclerView
    protected val wordListAdapter = WordListAdapter()

    private fun initializeCandidatesView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
            adapter = wordListAdapter
        }
    }

    open fun init(kbView: KeyboardView, candidatesView: RecyclerView) {
        kbView.keyboard = T9Keyboard(context)
        kbView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSink
            )
        )
        this.candidatesView = candidatesView
        initializeCandidatesView(candidatesView)
        scope.launch {
            eventSink.send(Event.START.noData())
        }
    }

    abstract fun displayInfo(resId: Int, vararg formatArgs: Any)

    // TODO bytes -> percentage
    override fun showProgress(bytes: Int) {
        displayInfo(R.string.engine_loading, bytes)
    }

     private fun updateCandidates(candidates: Collection<String>) {
        wordListAdapter.update(candidates)
    }

    override fun showCandidates(candidates: Collection<String>) {
        log.d("View: showCandidates:$candidates")
        updateCandidates(candidates)
        //testingHook.onShowCandidates()
    }

    override fun showKeyboard() {
        log.w("View: TODO: showKeyboard")
        displayInfo(R.string.notify_initialized)
        //defaultSharedPreferences.edit().putLong("load_time", loadTime).apply()
    }

     private fun selectCandidate(selectedCandidate: Int) {
        wordListAdapter.select(selectedCandidate)
        @Suppress("ConstantConditionIf")
        if (preferences.autoScroll) {
            //candidatesView.scrollToPosition(wordListAdapter.selectedWord)
            // check candidates_view.xml
            (candidatesView.layoutManager as LinearLayoutManager).run {
                val selectedWord = wordListAdapter.selectedWord
                if (selectedWord !in findFirstCompletelyVisibleItemPosition()..findLastCompletelyVisibleItemPosition()) {
                    scrollToPositionWithOffset(selectedWord, 20)
                }
            }
        }
    }

    override fun highlightCandidate(selectedCandidate: Int) {
        selectCandidate(selectedCandidate)
    }

     private fun clearCandidates() {
        wordListAdapter.clear()
    }

    /**
     * TODO: Match methods with [android.view.inputmethod.InputConnection] (commitText etc).
     */
    override fun confirmInput(word: String) {
        log.d("View: confirmInput($word)")
        inputConnection.commitText(word, 1)
        clearCandidates()
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