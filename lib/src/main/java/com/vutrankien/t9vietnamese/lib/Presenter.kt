package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.engine.T9Engine
import kotlinx.coroutines.launch

class Presenter constructor(
    private val engineSeed: Sequence<String>,
    private val engine: T9Engine,
    private val lg: LogFactory
) {
    private val log = lg.newLog("Presenter")
    private lateinit var view: View
    internal var typingState: TypingState =
        TypingState.Init(this)
        set(value) {
            log.i("Presenter:TypingState:change: $field -> $value")
            field = value
        }

    init {
    }

    fun attachView(view: View) {
        view.scope.launch {
            receiveUiEvents(view)
        }
        view.scope.launch {
            receiveEngineEvents()
        }
    }

    private suspend fun receiveUiEvents(view: View) {
        this.view = view
        for (eventWithData in view.eventSource) {
            when (eventWithData.event) {
                Event.START -> {
                    log.i("Start initializing")
                    val startTime = System.currentTimeMillis()
                    view.showProgress()
                    engine.init(engineSeed)
                    view.showKeyboard()
                    val loadTime = (System.currentTimeMillis()
                            - startTime)
                    log.i("Initialization Completed! loadTime=$loadTime")
                }
                Event.KEY_PRESS -> {
                    engine.push(eventWithData.data ?:
                    throw IllegalStateException("UI KEY_PRESS event with null data!"))
                }
            }
        }
    }

    private suspend fun receiveEngineEvents() {
        for (event in engine.eventSource) {
            when(event) {
                is T9Engine.Event.Confirm -> view.confirmInput(event.word)
                is T9Engine.Event.NewCandidates -> view.showCandidates(event.candidates)
            }
        }
    }

    sealed class TypingState(lg: LogFactory) {
        protected val log = lg.newLog("TypingState")
        open suspend fun keyPress(engine: T9Engine, key: Key) {
            throw IllegalStateException("${javaClass.name}.keyPress($key)")
        }

        override fun toString(): String {
            return javaClass.simpleName
        }

        class Init(private val presenter: Presenter) : TypingState(presenter.lg) {
            override suspend fun keyPress(engine: T9Engine, key: Key) {
                presenter.typingState =
                    Typing(
                        presenter,
                        engine
                    )
            }
        }

        class Typing(private val presenter: Presenter, engine: T9Engine) : TypingState(presenter.lg) {
            override suspend fun keyPress(engine: T9Engine, key: Key) {
                log.d("keyPress:$key")
                engine.push(key)
            }
        }

        class Confirmed(presenter: Presenter, result: Set<String>) : TypingState(presenter.lg) {
            init {
                presenter.onTypingConfirmed(result)
            }
        }
    }

    private fun onTypingConfirmed(result: Set<String>) {
        view.showCandidates(result)
    }
}
