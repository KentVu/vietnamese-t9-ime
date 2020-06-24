package com.vutrankien.t9vietnamese.android

import com.android.inputmethod.keyboard.KeyboardActionListener
import com.android.inputmethod.latin.common.InputPointers
import com.vutrankien.t9vietnamese.lib.Event
import com.vutrankien.t9vietnamese.lib.EventWithData
import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.LogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

internal class KeyboardActionListener(
    logFactory: LogFactory,
    private val scope: CoroutineScope,
    private val eventSource: Channel<EventWithData<Event, Key>>
) : KeyboardActionListener {
    private val log: LogFactory.Log = logFactory.newLog("KeyboardActionListener")

    override fun onPressKey(primaryCode: Int, repeatCount: Int, isSinglePointer: Boolean) {
        log.d("onPressKey:$primaryCode")
    }

    override fun onReleaseKey(primaryCode: Int, withSliding: Boolean) {
        log.d("onReleaseKey:$primaryCode")
    }

    override fun onCodeInput(primaryCode: Int, x: Int, y: Int, isKeyRepeat: Boolean) {
        log.d("onCodeInput:$primaryCode")
    }

    override fun onTextInput(text: String?) {
        log.d("onTextInput:$text")
        if(text == null) {
            log.w("onTextInput:text is null!!")
            return
        }
        scope.launch {
            eventSource.send(
                Event.KEY_PRESS.withData(
                    Key.fromNum(text[0])
                ))
        }
    }

    override fun onStartBatchInput() {
        log.d("onStartBatchInput")
    }

    override fun onUpdateBatchInput(batchPointers: InputPointers?) {
        log.d("onUpdateBatchInput:$batchPointers")
    }

    override fun onEndBatchInput(batchPointers: InputPointers?) {
        log.d("onEndBatchInput:$batchPointers")
    }

    override fun onCancelBatchInput() {
        log.d("onCancelBatchInput")
    }

    override fun onCancelInput() {
        log.d("onCancelInput")
    }

    override fun onFinishSlidingInput() {
        log.d("onFinishSlidingInput")
    }

    override fun onCustomRequest(requestCode: Int): Boolean {
        log.d("onCustomRequest")
        return false
    }
}