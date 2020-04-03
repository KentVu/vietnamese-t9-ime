/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vutrankien.t9vietnamese.android

import android.inputmethodservice.KeyboardView
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
            eventSource.send(
                Event.KEY_PRESS.withData(
                    Key.fromNum(text[0])
                ))
        }
    }
}