package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.KeysCollection
import com.github.kentvu.t9vietnamese.model.WordList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class TypingTest() {

    //private val app: T9Vietnamese = T9Vietnamese()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun typingTestSmall() = runTest {
        //val robot = Robot(App())
        //robot.type('1')
        //robot.checkCandidateHas('a', 'b', 'c')
        val app = App(
            Keyboard(listOf(Key('1', "a"))),
            WordList.Default(setOf("aa", "bb2", "cc1", "dd2"))
        )
        app.init()
        app.type('1')
        assertContains(app.candidates, "aa")
    }

}