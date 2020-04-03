/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import io.kotlintest.IsolationMode
import io.kotlintest.TestCase
import io.kotlintest.specs.FunSpec
import io.mockk.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel

class PresenterTest: FunSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val seed: Sequence<String> = "a\nb\nc".lineSequence()
    private lateinit var view: View
    private lateinit var engine: T9Engine
    private lateinit var env: Env

    override fun beforeTest(testCase: TestCase) {
        view = mockk(relaxUnitFun = true)
        every { view.eventSource } returns Channel()
        every { view.scope } returns GlobalScope

        env = mockk()
        //every { env.... } returns ...

        engine = mockk(relaxUnitFun = true)
        every { engine.eventSource } returns Channel()
        every { engine.canReuseDb() } returns false
    }

    //val logGenerator = daggerComponents.logGenerator()
    private fun getPresenter(): Presenter {
        return DaggerPresenterComponents.builder()
            .presenterModule(
                PresenterModule(
                    seed,
                    engine,
                    env
                )
            )
            .build()
            .presenter()
    }

    init {
        test("showProgressIndicatorOnStart") {
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            verify(timeout = 100) { view.showProgress(any()) }
        }

        test("initializeEngineOnStart") {
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            coVerify { engine.init(seed) }
        }

        test("6.ReuseBuiltDawg") {
            every { engine.canReuseDb() } returns true
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            coVerify { engine.initFromDb() }
        }

        test("showKeyboardWhenEngineLoadCompleted") {
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            verify(timeout = 100) { view.showKeyboard() }
        }

        test("whenTypeOneNumberThenDisplayResult") {
            getPresenter().attachView(view)
            val cand = setOf("4")
            setupEngine(mapOf(Key.num0 to T9Engine.Event.Confirm("4"))) {T9Engine.Event.NewCandidates(cand)}
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num4))
            verify(timeout = 10) { view.showCandidates(cand) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num2))
            verify(timeout = 1000) { view.showCandidates(cand) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num0))
            verify { view.confirmInput("4") }
        }

        test("Confirm input") {
            getPresenter().attachView(view)
            val candidates = setOf("5")
            setupEngine(mapOf(Key.num0 to T9Engine.Event.Confirm("5")),
                {T9Engine.Event.NewCandidates(candidates)})

            view.eventSource.send(Event.KEY_PRESS.withData(Key.num4))
            verify(timeout = 10) { view.showCandidates(candidates) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num2))
            verify(timeout = 1000) { view.showCandidates(candidates) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num0))
            verify { view.confirmInput("5") }
        }
    }

    private fun setupEngine(config: Map<Key, T9Engine.Event>, fallback: () -> T9Engine.Event) {
        coEvery {
            engine.push(any())
        } coAnswers {
            engine.eventSource.send(config[firstArg()] ?: fallback())
        }
    }
}

