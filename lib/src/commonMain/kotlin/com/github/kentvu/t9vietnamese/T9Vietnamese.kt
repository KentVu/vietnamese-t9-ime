package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.View

abstract class T9Vietnamese() {
    abstract val view: View
    abstract val keyPad: KeyPad

    abstract suspend fun type(cs: String)
}
