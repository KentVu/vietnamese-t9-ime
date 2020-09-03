package com.vutrankien.t9vietnamese.android

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import com.vutrankien.t9vietnamese.lib.View as MVPView

/**
 * Created by vutrankien on 17/05/02.
 */
class T9Vietnamese : InputMethodService(), MVPView {
    @Inject
    lateinit var logFactory: LogFactory
    private lateinit var log: LogFactory.Log
    @Inject
    lateinit var presenter: Presenter
    override val scope = CoroutineScope(Dispatchers.Main + Job())
    override val eventSource: Channel<EventWithData<Event, Key>> =
        Channel()
    private val logic: UiLogic = UiLogic.DefaultUiLogic()
    //private val wordListAdapter = WordListAdapter()

    override fun onCreate() {
        super.onCreate()
        (application as T9Application).appComponent.inject(this)
        log = logFactory.newLog("T9IMService")
        log.d("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel("T9Vietnamese.onDestroy()")
    }

    override fun onCreateInputView(): View {
        val inputView = layoutInflater.inflate(
            R.layout.input, null) as (T9KeyboardView)
        inputView.keyboard = T9Keyboard(this, R.xml.t9)
        inputView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSource
            )
        )
        presenter.attachView(this)
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

    override fun nextCandidate() {
        logic.nextCandidate()
    }

    override fun confirmInput(word: String) {
        log.d("View: confirmInput:$word")
        // XXX Is inserting a space here a right place?
        currentInputConnection.commitText(" $word", 1)
        //setCandidatesViewShown(false)
        logic.clearCandidates()
        //findViewById<EditText>(R.id.editText).append(" $word")
        //wordListAdapter.clear()
    }

    fun onBtnClick(view: View) {
        log.d("onBtnClick() btn=" + (view as Button).text)
    }
}