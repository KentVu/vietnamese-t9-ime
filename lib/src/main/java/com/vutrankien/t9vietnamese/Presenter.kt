package com.vutrankien.t9vietnamese

import kotlinx.coroutines.launch

class Presenter(val engine: T9Engine) {
    private lateinit var view: View
    internal var typingState: TypingState = TypingState.Init(this)
        set(value) {
            println("Presenter:TypingState:change: $field -> $value")
            field = value
        }


    fun attachView(view: View) {
        receiveEvents(view)
    }

    private fun receiveEvents(view: View) {
        this.view = view
        view.scope.launch {
            for (eventWithData in view.eventSource) {
                when (eventWithData.event) {
                    Event.START -> {
                        view.showProgress()
                        engine.init().await()
                        view.showKeyboard()
                    }
                    Event.KEY_PRESS -> {
                        if (typingState is TypingState.Init) {
                            typingState = TypingState.Typing(this@Presenter, engine)
                        }
                        typingState.keyPress(engine, eventWithData.data ?: error("NULL data: $eventWithData"))
                    }
                }
            }
        }
    }

    sealed class TypingState {
        open fun keyPress(engine: T9Engine, key: Char) {
            throw IllegalStateException("${javaClass.name}.keyPress($key)")
        }

        override fun toString(): String {
            return javaClass.simpleName
        }

        class Init(val presenter: Presenter) : TypingState() {
            override fun keyPress(engine: T9Engine, key: Char) {
                presenter.typingState = Typing(presenter, engine)
            }
        }

        class Typing(private val presenter: Presenter, engine: T9Engine) : TypingState() {
            val input: T9Engine.Input = engine.startInput()
            override fun keyPress(engine: T9Engine, key: Char) {
                if (key != ' ') {
                    input.input(key)
                } else {
                    presenter.typingState = Confirmed(presenter, input.result())
                }
            }

        }

        class Confirmed(presenter: Presenter, result: List<String>) : TypingState() {
            init {
                presenter.onTypingConfirmed(result)
            }
        }
    }

    private fun onTypingConfirmed(result: List<String>) {
        view.showCandidates(result)
    }
}
