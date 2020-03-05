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

    //val logGenerator = daggerComponents.logGenerator()
    fun getPresenter(): Presenter {
        return DaggerPresenterComponents.builder()
            .presenterModule(PresenterModule(seed, engine))
            .build()
            .presenter()
    }

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
        getPresenter().attachView(view)
        view.eventSource.send(Event.START.noData())
        verify(timeout = 100) { view.showProgress() }
    }

    @Test
    fun initializeEngineOnStart() = runBlocking {
        getPresenter().run {
            attachView(view)
        }
        view.eventSource.send(Event.START.noData())
    }

    @Test
    fun showKeyboardWhenEngineLoadCompleted() = runBlocking {
        getPresenter().attachView(view)
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
        getPresenter().attachView(view)

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

