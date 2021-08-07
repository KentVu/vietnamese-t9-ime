package com.vutrankien.t9vietnamese.android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.AndroidLogFactory
import com.vutrankien.t9vietnamese.android.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpecsTest: MainActivityTestsBase(
        AndroidLogFactory.newLog("SpecsTest")
) {
    @Before
    fun setup() {
        unlockScreen()
    }

    @Test
    fun selectPrevCandidateWhenNotTypingThenNothingInserted(): Unit = runBlocking {
        robot.selectPrev()
                .selectPrev()
                .checkNoCandidatesDisplayed()
                .confirm()
                .checkInsertedTextIs(" ")
        // wait if necessary
    }

    @Test
    fun deletePreviousWordWhenNotTyping(): Unit = runBlocking {
        with(robot) {
            pressSequentially("24236")
            checkCandidateDisplayed("chào")
            browseTo("chào")
            confirm()
            backspace()
            checkInsertedTextIs("chà")
        }
        // wait if necessary
    }
}