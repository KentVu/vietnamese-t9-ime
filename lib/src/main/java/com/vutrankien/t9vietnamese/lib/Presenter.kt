package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.engine.T9Engine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class Presenter(
    lg: LogFactory,
    private val engine: T9Engine,
    private val view: View
) {
    private val log = lg.newLog("Presenter")

    fun start() {
        view.scope.launch {
            receiveUiEvents()
        }
        view.scope.launch {
            receiveEngineEvents()
        }
    }

    private suspend fun receiveUiEvents() {
        for (eventWithData in view.eventSource) {
            //log.d("receiveEvents:$eventWithData")
            when (eventWithData.event) {
                Event.START -> {
                    log.i("Start initializing")
                    view.showProgress(0)
                    val loadTime = measureTimeMillis {
                        engine.init()
                    }
                    log.i("Initialization Completed! loadTime=$loadTime")
                    view.showKeyboard()
                }
                Event.KEY_PRESS -> {
                    withContext(Dispatchers.Default) {
                        engine.push(eventWithData.data ?:
                        throw IllegalStateException("UI KEY_PRESS event with null data!"))
                    }
                }
            }
        }
    }

    private suspend fun receiveEngineEvents() {
        for (event in engine.eventSource) {
            log.v("receiveEngineEvents:$event")
            when(event) {
                is T9Engine.Event.LoadProgress -> view.showProgress(event.bytes)
                is T9Engine.Event.Confirm -> view.confirmInput(event.word)
                is T9Engine.Event.NewCandidates -> view.showCandidates(event.candidates)
                is T9Engine.Event.SelectCandidate -> view.highlightCandidate(event.selectedCandidate)
                is T9Engine.Event.Backspace -> view.deleteBackward()
            }
        }
    }

}
