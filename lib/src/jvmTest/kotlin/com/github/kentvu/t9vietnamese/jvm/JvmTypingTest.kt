package com.github.kentvu.t9vietnamese.jvm

import assertk.assertThat
import assertk.assertions.containsAll
import com.github.kentvu.t9vietnamese.VNT9App
import com.github.kentvu.t9vietnamese.model.*
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertContains

class JvmTypingTest() {

    @Test
    fun typingTest() {
        val app: T9App = VNT9App(
            VietnameseWordList,
            FileSystem.SYSTEM
        )
        app.init()
        app.type('2')
        assertContains(app.candidates, "2")
        app.type('4')
        assertContains(app.candidates, "24")
        app.type('2')
        assertThat(app.candidates).containsAll(
            "cha", "chà", "chào"
        )
    }

}