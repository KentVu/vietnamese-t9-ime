package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputConnection
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore

/**
 * Created by vutrankien on 17/05/02.
 */
class T9Vietnamese : InputMethodService() {
    private val logFactory: LogFactory = AndroidLogFactory
    private val log = logFactory.newLog("T9IMService")

    lateinit var view: InputMethodServiceView
    private lateinit var presenter: Presenter
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val channel: Channel<EventWithData<Event, Key>> = Channel()

    class InputMethodServiceView(
        logFactory: LogFactory,
        context: Context,
        override val scope: CoroutineScope,
        channel: Channel<EventWithData<Event, Key>>,
        private val inputMethodService: InputMethodService
    ) : AndroidView(
        logFactory, logFactory.newLog("T9Vietnamese.V"),
        context, scope,
        channel, channel
    ) {
        private var lastInputConnection: InputConnection? = null
        override val inputConnection: InputConnection
            get() {
                val newInputConnection = inputMethodService.currentInputConnection
                if (lastInputConnection != newInputConnection) {
                    log.d("inputConnection#get():new $newInputConnection")
                }
                lastInputConnection = newInputConnection
                return newInputConnection
            }

        override fun displayInfo(resId: Int, vararg formatArgs: Any) {
            log.i(context.getString(resId, *formatArgs))
        }
    }

    private lateinit var inputView: T9KeyboardView
    lateinit var candidatesView: RecyclerView

    override fun onCreate() {
        super.onCreate()
        log.d("onCreate")
        inputView = layoutInflater.inflate(
            R.layout.input, null) as (T9KeyboardView)
        candidatesView = (layoutInflater.inflate(R.layout.candidates_view, null) as RecyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel("T9Vietnamese.onDestroy()")
    }

    override fun onCreateInputView(): View {
        inputView.keyboard = T9Keyboard(this)
        inputView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                channel
            )
        )
        view = InputMethodServiceView(
            logFactory,
            this,
            scope,
            channel,
            this
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
//        scope.launch {
//            repeat(2) { semaphore.acquire() }
//        }
        view.init(inputView, candidatesView)
        presenter.start()
        return inputView
        //return layoutInflater.inflate(
        //    R.layout.dialpad_table_old, null
        //) as ConstraintLayout
    }

    object waitViews {
        fun start() {
            val semaphore = Semaphore(1, 1)
        }
    }
    override fun onCreateCandidatesView(): View {
        setCandidatesViewShown(true)
        log.d("onCreateCandidatesView:$candidatesView")
        return candidatesView
    }
}