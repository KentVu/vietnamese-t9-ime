package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.T9Engine
import kotlinx.coroutines.launch

class Presenter(val engine: T9Engine) {
    fun attachView(view: View) {
        receiveEvents(view)
    }

    private fun receiveEvents(view: View) {
        view.scope.launch {
            when (view.eventSource.receive()) {
                Event.START -> {
                    view.showProgress()
                    engine.init().await()
                    view.showKeyboard()
                }
            }
        }
    }

}
