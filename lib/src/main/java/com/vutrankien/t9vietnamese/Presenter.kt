package com.vutrankien.t9vietnamese

import kotlinx.coroutines.launch

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
        open fun keyPress(engine: T9Engine, key: Char) {
            throw IllegalStateException("${javaClass.name}.keyPress($key)")
        }

        override fun toString(): String {
            return javaClass.simpleName
        }

        class Manager : State() {
            internal var state: State = Init(this)
                set(value) {
                    println("Presenter:State@Manager:changeState: $field -> $value")
                    field = value
                }

            override fun keyPress(engine: T9Engine, key: Char) {
                if (state is Init) {
                    state = Typing(this, engine)
                }
                state.keyPress(engine, key)
            }

        }

        class Init(val mgr: Manager) : State() {
            override fun keyPress(engine: T9Engine, key: Char) {
                mgr.state = Typing(mgr, engine)
            }
        }

        class Typing(private val mgr: Manager, engine: T9Engine) : State() {
            val input: T9Engine.Input = engine.startInput()
            override fun keyPress(engine: T9Engine, key: Char) {
                if (key != ' ') {
                    input.input(key)
                } else {
                    mgr.state = Confirmed(mgr, input.result())
                }
            }

        }

        class Confirmed(mgr: Manager, result: List<String>) : State() {
            init {
                mgr.
            }
        }
    }
}
