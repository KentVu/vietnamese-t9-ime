package com.github.kentvu.t9vietnamese

import app.cash.turbine.test
import com.github.kentvu.t9vietnamese.jvm.JvmT9Vietnamese
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TypingTest() {

    //private val app: T9Vietnamese = T9Vietnamese()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun typingTest() = runTest {
        val app = JvmT9Vietnamese()
        app.view.candidates.test {
            app.type('2', '4','2','3','6')
            assertTrue(app.view.candidates.value.has("chào"))
        }
    }

}