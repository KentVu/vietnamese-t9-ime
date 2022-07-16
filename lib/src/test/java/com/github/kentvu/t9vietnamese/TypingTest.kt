package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.StandardKeys
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TypingTest() {

    private val app: T9Vietnamese = T9Vietnamese(
        keyPad,
        view
    )

    @Before
    fun setup() {
    }

    @Test
    fun typingTest() {
        app.type('2', '4','2','3','6')
        assertTrue(app.view.candidates.has("chào"))
    }

}