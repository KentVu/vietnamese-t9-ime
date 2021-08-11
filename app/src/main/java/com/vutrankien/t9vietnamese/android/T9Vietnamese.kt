package com.vutrankien.t9vietnamese.android

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import com.vutrankien.t9vietnamese.lib.View as MVPView

/**
 * Created by vutrankien on 17/05/02.
 */
class T9Vietnamese : InputMethodService(), MVPView {
    private val logFactory: LogFactory = AndroidLogFactory
    private val log = logFactory.newLog("T9IMService")
    private lateinit var presenter: Presenter
    override val scope = CoroutineScope(Dispatchers.Main + Job())
    override val eventSource: Channel<EventWithData<Event, Key>> =
        Channel()
    private val logic: UiLogic by lazy { UiLogic.DefaultUiLogic(Preferences(applicationContext)) }
    //private val wordListAdapter = WordListAdapter()

    override fun onCreate() {
        super.onCreate()
        presenter = Presenter(
            logFactory,
            DefaultT9Engine(
                DecomposedSeed(resources),
                VnPad,
                logFactory,
                TrieDb(logFactory, AndroidEnv(applicationContext))
            ),
            this
        )
        log.d("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel("T9Vietnamese.onDestroy()")
    }

    override fun onCreateInputView(): View {
        val inputView = layoutInflater.inflate(
            R.layout.input, null) as (T9KeyboardView)
        inputView.keyboard = T9Keyboard(this)
        inputView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSource
            )
        )
        presenter.receiveEvents()
        scope.launch {
            eventSource.send(Event.START.noData())
        }
        return inputView
        //return layoutInflater.inflate(
        //    R.layout.dialpad_table_old, null
        //) as ConstraintLayout
    }

    override fun onCreateCandidatesView(): View {
        setCandidatesViewShown(true)
        return (layoutInflater.inflate(R.layout.candidates_view, null) as RecyclerView).also {
            log.d("onCreateCandidatesView:$it")
            logic.initializeCandidatesView(it)
        }
    }

    override fun showProgress(bytes: Int) {
        //log.w("TODO: showProgress")
        //displayInfo(R.string.engine_loading, bytes)
    }

    override fun showKeyboard() {
        log.w("View: TODO: showKeyboard")
        //displayInfo(R.string.notify_initialized)
    }

    override fun showCandidates(candidates: Collection<String>) {
        log.d("View: TODO: showCandidates:$candidates")
        logic.updateCandidates(candidates)
    }

    override fun candidateSelected(selectedCandidate: Int) {
        logic.selectCandidate(selectedCandidate)
    }

    /**
     * TODO: Match methods with [android.view.inputmethod.InputConnection].
     */
    override fun confirmInput(word: String) {
        log.d("View: confirmInput:$word")
        currentInputConnection.commitText(word, 1)
        logic.clearCandidates()
    }

    override fun deleteBackward() {
        log.d("deleteBackward")
        currentInputConnection.deleteSurroundingText(1, 0)
    }

    fun onBtnClick(view: View) {
        log.d("onBtnClick() btn=" + (view as Button).text)
    }
}