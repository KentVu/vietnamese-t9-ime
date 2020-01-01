package com.vutrankien.t9vietnamese

import kotlinx.coroutines.launch

class Presenter(val engine: T9Engine) {
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
                    else -> System.err.println("unknown event:$eventWithData")
                }
            }
        }
    }

}
