package com.github.kentvu.t9vietnamese.desktop

import AppUi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.UIEvent
import com.github.kentvu.t9vietnamese.model.VNKeys
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class DesktopUI(
    private val exitApplication: () -> Unit,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : UI {
    internal val eventSource = MutableSharedFlow<UIEvent>(extraBufferCapacity = 1)
    private var keysEnabled = MutableStateFlow(false)

    @Composable
    internal fun buildUi() {
        AppUi(keysEnabled) { key ->
            eventSource.tryEmit(UIEvent.KeyPress(key))
        }
    }

    internal fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.isCtrlQ()) {
            exitApplication()
            return true
        }
        return onUserEvent(keyEvent)
    }

    override fun subscribeEvents(block: (UIEvent) -> Unit) {
        eventSource.onEach {
            block(it)
        }.launchIn(scope)
    }

    override fun update(event: UI.UpdateEvent) {
        when(event) {
            is UI.UpdateEvent.Initialized -> {
                keysEnabled.update { true }
            }
            is UI.UpdateEvent.NewCandidates -> {
                Napier.d("NewCandidates: ${event.candidates}")
            }
            UI.UpdateEvent.Close -> exitApplication()
        }
    }

    /**
     * @return event handled.
     */
    fun onUserEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type == KeyEventType.KeyUp) {
            if (Letter2Keypad.available(keyEvent.key)) {
                eventSource.tryEmit(UIEvent.KeyPress(
                    VNKeys.fromChar(
                        Letter2Keypad.numForKey(keyEvent.key)!!)))
                return true
            }
        }
        // let other handlers receive this event
        return false
    }

    object Letter2Keypad {
        @OptIn(ExperimentalComposeUiApi::class)
        private val map = mapOf(
            Key.Zero to '0',
            Key.One to '1',
            Key.Two to '2',
            Key.Three to '3',
            Key.Four to '4',
            Key.Five to '5',
            Key.Six to '6',
            Key.Seven to '7',
            Key.Eight to '8',
            Key.Nine to '9',
            // Next is for simulating a keypad by left-side of the keyboard.
            Key.Spacebar to '0',
            Key.Q to '1',
            Key.W to '2',
            Key.E to '3',
            Key.A to '4',
            Key.S to '5',
            Key.D to '6',
            Key.Z to '7',
            Key.X to '8',
            Key.C to '9',
        )
        fun available(key: Key): Boolean {
            return map.containsKey(key)
        }

        fun numForKey(key: Key): Char? {
            return map[key]
        }

    }

}

@OptIn(ExperimentalComposeUiApi::class)
private fun KeyEvent.isCtrlQ(): Boolean {
    return type == KeyEventType.KeyUp && isCtrlPressed && key == Key.Q
}
