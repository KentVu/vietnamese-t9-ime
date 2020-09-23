package com.vutrankien.t9vietnamese.android

import android.inputmethodservice.KeyboardView
import com.vutrankien.t9vietnamese.lib.Event
import com.vutrankien.t9vietnamese.lib.EventWithData
import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.LogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

internal class KeyboardActionListener(
    logFactory: LogFactory,
    private val scope: CoroutineScope,
    private val eventSink: SendChannel<EventWithData<Event, Key>>
) : KeyboardView.OnKeyboardActionListener {
    private val log: LogFactory.Log = logFactory.newLog("KeyboardActionListener")
    override fun swipeRight() {
        log.d("swipeRight")
    }

    override fun onPress(primaryCode: Int) {
        log.d("onPress")
    }

    override fun onRelease(primaryCode: Int) {
        log.d("onRelease")
    }

    override fun swipeLeft() {
        log.d("swipeLeft")
    }

    override fun swipeUp() {
        log.d("swipeUp")
    }

    override fun swipeDown() {
        log.d("swipeDown")
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        log.d("onKey:$primaryCode,${keyCodes?.joinToString()}")
    }

    override fun onText(text: CharSequence?) {
        log.d("onText:$text")
        if(text == null) {
            log.w("onText:text is null!!")
            return
        }
        scope.launch {
            eventSink.send(
                Event.KEY_PRESS.withData(
                    Key.fromNum(text[0])
                ))
        }
    }
}