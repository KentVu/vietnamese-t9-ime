package com.vutrankien.t9vietnamese

import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class Presenter(val engine: T9Engine) {
    private var stateMgr: State = State.Manager()

    fun attachView(view: View) {
        receiveEvents(view)
    }

    private fun receiveEvents(view: View) {
        view.scope.launch {
            for (eventWithData in view.eventSource) {
                when (eventWithData.event) {
                    Event.START -> {
                        view.showProgress()
                        engine.init().await()
                        view.showKeyboard()
                    }
                    Event.KEY_PRESS -> {
                        stateMgr.keyPress(engine, eventWithData.data ?: error("NULL data: $eventWithData"))
                    }
                    else -> System.err.println("unknown event:$eventWithData")
                }
            }
        }
    }

    sealed class State {
        abstract fun keyPress(engine: T9Engine, key: Char)

        class Manager : State() {
            private var state: State = Init()

            override fun keyPress(engine: T9Engine, key: Char) {
                if (state is Init) {
                    state = Typing(engine)
                }
                state.keyPress(engine, key)
            }

        }

        class Init: State() {
            override fun keyPress(engine: T9Engine, key: Char) {
                throw IllegalStateException("${javaClass.name}.keyPress($key)")
            }
        }

        class Typing(engine: T9Engine) : State() {
            val input: T9Engine.Input = engine.startInput()
            override fun keyPress(engine: T9Engine, key: Char) {
                if (key != ' ') {
                    input.input(key)
                }
            }

        }
    }
}
