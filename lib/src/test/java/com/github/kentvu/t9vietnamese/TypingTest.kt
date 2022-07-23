package com.github.kentvu.t9vietnamese

import app.cash.turbine.test
import com.github.kentvu.t9vietnamese.jvm.DummyView
import com.github.kentvu.t9vietnamese.jvm.JvmT9Vietnamese
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TypingTest() {

    private val app: T9Vietnamese = T9Vietnamese()

    @Before
    fun setup() {
    }

    @Test
    suspend fun typingTest() = runTest {
        val keyPad = DummyKeyPad()
        val view = DummyView()
        val app = JvmT9Vietnamese(keyPad, view)
        app.view.candidates.test {
            app.type('2', '4','2','3','6')
            assertTrue(app.view.candidates.value.has("chào"))
        }
    }

}