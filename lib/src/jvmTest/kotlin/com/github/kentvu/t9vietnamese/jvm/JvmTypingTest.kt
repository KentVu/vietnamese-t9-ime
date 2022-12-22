package com.github.kentvu.t9vietnamese.jvm

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsAll
import com.github.kentvu.t9vietnamese.lib.T9App
import com.github.kentvu.t9vietnamese.lib.VNT9App
import com.github.kentvu.t9vietnamese.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class JvmTypingTest() {

    @Test
    fun typingTest() = runTest {
        val app: T9App = VNT9App(
            VietnameseWordList,
            FileSystem.SYSTEM
        )
        app.init()
        app.eventFlow.test {
            app.type('2')
            awaitItem().also { item ->
                assertIs<T9AppEvent.UpdateCandidates>(item)
                //if (item !is T9AppEvent.UpdateCandidates) not needed
                assertContains(item.candidates, "2")
            }
            app.type('4')
            awaitItem().also { item ->
                assertIs<T9AppEvent.UpdateCandidates>(item)
                assertContains(item.candidates, "24")
            }
            app.type('2')
            awaitItem().also { item ->
                assertIs<T9AppEvent.UpdateCandidates>(item)
                assertThat(item.candidates).containsAll(
                    "cha", "chà", "chào"
                )
            }
            app.type("36")
            repeat(2) {
                awaitItem().also { item ->
                    assertIs<T9AppEvent.UpdateCandidates>(item)
                    assertThat(item.candidates).contains("chào")
                }
            }
        }
    }

}