package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.WordList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class TypingTest() {

    //private val app: T9Vietnamese = T9Vietnamese()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun typingTest() = runTest {
        //val robot = Robot(App())
        //robot.type('1')
        //robot.checkCandidateHas('a', 'b', 'c')
        val app = App(
            Keyboard(Key('1', "a")),
            WordList.Default(setOf("aa", "bb2", "cc1", "dd2"))
        )
        app.type('1')
        app.verifyCandidatesContainOnly("aa")
    }

}