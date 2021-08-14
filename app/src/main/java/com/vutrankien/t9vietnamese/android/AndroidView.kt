package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.inputmethodservice.KeyboardView
import com.vutrankien.t9vietnamese.lib.Event
import com.vutrankien.t9vietnamese.lib.EventWithData
import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel

abstract class AndroidView(
    protected val context: Context,
    override val scope: CoroutineScope,
    override val eventSource: ReceiveChannel<EventWithData<Event, Key>>
) : View {
    private val preferences by lazy { Preferences(context.applicationContext) }
    protected val logic: UiLogic by lazy { UiLogic.DefaultUiLogic(preferences) }
}