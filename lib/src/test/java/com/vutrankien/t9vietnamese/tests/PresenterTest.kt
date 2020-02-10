package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.*
import com.vutrankien.t9vietnamese.engine.T9Engine
import io.kotlintest.IsolationMode
import io.kotlintest.specs.AnnotationSpec
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class PresenterTest: AnnotationSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val seed: Sequence<String> = "a\nb\nc".lineSequence()
    lateinit var view: View
    lateinit var engine: T9Engine

    @Before
    fun setUp() {
        view = mockk(relaxUnitFun = true)
        every { view.eventSource } returns Channel()
        every { view.scope } returns GlobalScope

        engine = mockk(relaxUnitFun = true)
        every { engine.eventSource } returns Channel()
    }

    @Test
    fun showProgressIndicatorOnStart() = runBlocking {
        Presenter(seed, engine).attachView(view)
        view.eventSource.send(Event.START.noData())
        verify(timeout = 100) { view.showProgress() }
    }

    @Test
    fun initializeEngineOnStart() = runBlocking {
        Presenter(seed, engine).run {
            attachView(view)
        }
        view.eventSource.send(Event.START.noData())
    }

    @Test
    fun showKeyboardWhenEngineLoadCompleted() = runBlocking {
        Presenter(seed, engine).attachView(view)
        view.eventSource.send(Event.START.noData())
        verify(timeout = 100) { view.showKeyboard() }
    }

    @Test
    fun whenTypeOneNumberThenDisplayResult() = runBlocking {
        val cand = setOf("4")
        //engine = MockEngine()
        every {
            engine.push(any())
        } coAnswers {
            //GlobalScope.launch {
            when (firstArg<Key>()) {
                Key.num0 -> engine.eventSource.send(T9Engine.Event.Confirm)
                else -> engine.eventSource.send(T9Engine.Event.NewCandidates(cand))
            }
            //}
        }
        Presenter(seed, engine).attachView(view)

        view.eventSource.send(Event.KEY_PRESS.withData(Key.num4))
        verify(timeout = 10) { view.showCandidates(cand) }
        view.eventSource.send(Event.KEY_PRESS.withData(Key.num2))
        verify(timeout = 1000) { view.showCandidates(cand) }
        view.eventSource.send(Event.KEY_PRESS.withData(Key.num0))
        verify { view.confirmInput() }
        //withTimeout(3000) {
        //}
    }
}

class MockEngine : T9Engine {
    override var initialized: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override val pad: PadConfiguration
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val eventSource: Channel<T9Engine.Event>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override suspend fun init(seed: Sequence<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun push(key: Key) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val candidates: Set<String>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

}

