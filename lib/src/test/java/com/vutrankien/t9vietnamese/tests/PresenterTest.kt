package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.*
import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.trie.Trie
import io.kotlintest.IsolationMode
import io.kotlintest.specs.AnnotationSpec
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class PresenterTest: AnnotationSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    lateinit var view: View
    lateinit var trie: Trie
    lateinit var engine: T9Engine

    @Before
    fun setUp() {
        view = mockk(relaxUnitFun = true)
        every { view.eventSource } returns Channel()
        every { view.scope } returns GlobalScope

        trie = mockk()
        coEvery { trie.build(any(), any()) } just Runs
        engine = mockk(relaxUnitFun = true)
    }

    @Test
    fun showProgressIndicatorOnStart() = runBlocking {
        withTimeout(1000) {
            Presenter(trie, engine).attachView(view)
            view.eventSource.send(Event.START.noData())
            verify { view.showProgress() }
        }
    }

    @Suppress("DeferredResultUnused") // just verify init has called
    @Test
    fun initializeEngineOnStart() = runBlocking {
        Presenter(trie, engine).run {
            attachView(view)
            input = "a\nb\nc".byteInputStream()
        }
        view.eventSource.send(Event.START.noData())
        coVerify { trie.build(any()) }
    }

    @Test
    fun showKeyboardWhenEngineLoadCompleted() = runBlocking {
        Presenter(trie, engine).attachView(view)
        view.eventSource.send(Event.START.noData())
        verify(timeout = 100) { view.showKeyboard() }
    }

    @Test
    fun whenTypeOneNumberThenDisplayResult() = runBlocking {
        withTimeout(3000) {
            val input = mockk<T9Engine.Input>()
            every { engine.startInput() } returns input
            every {
                input.push(any())
            } coAnswers {
                when (firstArg<Key>()) {
                    Key.num0 -> engine.eventSource.send(T9Engine.Event.CONFIRM)
                    else -> engine.eventSource.send(T9Engine.Event.NEW_CANDIDATES)
                }
            }
            Presenter(trie, engine).attachView(view)
            val cand = setOf("4")
            every { input.candidates } returns cand
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num4))
            verify { view.showCandidates(cand) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num2))
            verify { view.showCandidates(cand) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num0))
            verify { view.confirmInput() }
        }
    }
}

