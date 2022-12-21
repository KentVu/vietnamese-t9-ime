package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.WordList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertContains

class TypingTest() {

    //private val app: T9App = T9App()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun typingTestSmall() = runTest {
        //val robot = Robot(App())
        //robot.type('1')
        //robot.checkCandidateHas('a', 'b', 'c')
        val app = VNT9App(
            KeyPad(listOf(Key('1', "a"))),
            WordList.Default(setOf("aa", "bb2", "cc1", "dd2")),
            FakeFileSystem()
        )
        app.init()
        app.type('1')
        assertContains(app.candidates, "a")
        app.type('1')
        assertContains(app.candidates, "aa")
    }

}