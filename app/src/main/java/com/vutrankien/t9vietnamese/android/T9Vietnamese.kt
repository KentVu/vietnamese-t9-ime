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

package com.vutrankien.t9vietnamese.android

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.vutrankien.t9vietnamese.lib.View as MVPView

/**
 * Created by vutrankien on 17/05/02.
 */
class T9Vietnamese : InputMethodService(), MVPView {
    @Inject
    lateinit var logFactory: LogFactory
    private lateinit var log: LogFactory.Log
    @Inject
    lateinit var presenter: Presenter
    override val scope = CoroutineScope(Dispatchers.Main + Job())
    override val eventSource: Channel<EventWithData<Event, Key>> =
        Channel()
    private val logic: UiLogic = UiLogic.DefaultUiLogic()
    //private val wordListAdapter = WordListAdapter()

    override fun onCreate() {
        super.onCreate()
        (application as T9Application).appComponent.inject(this)
        log = logFactory.newLog("T9IMService")
        log.d("onCreate")
    }

    override fun onCreateInputView(): View {
        val inputView = layoutInflater.inflate(
            R.layout.input, null) as (T9KeyboardView)
        inputView.keyboard = T9Keyboard(this, R.xml.t9)
        inputView.setOnKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSource
            )
        )
        presenter.attachView(this)
        scope.launch {
            eventSource.send(Event.START.noData())
        }
        return inputView
        //return layoutInflater.inflate(
        //    R.layout.dialpad_table_old, null
        //) as ConstraintLayout
    }

    override fun onCreateCandidatesView(): View {
        return (layoutInflater.inflate(R.layout.candidates_view, null) as RecyclerView).also {
            log.d("onCreateCandidatesView:$it")
            logic.initializeCandidatesView(it)
        }
        //return (layoutInflater.inflate(R.layout.candidates_view, null) as RecyclerView).apply {
        //    layoutManager = LinearLayoutManager(this@T9Vietnamese, RecyclerView.HORIZONTAL, false)
        //}
    }

    override fun showProgress(bytes: Int) {
        //log.w("TODO: showProgress")
        //displayInfo(R.string.engine_loading, bytes)
    }

    override fun showKeyboard() {
        log.w("View: TODO: showKeyboard")
        //displayInfo(R.string.notify_initialized)
    }

    override fun showCandidates(candidates: Collection<String>) {
        log.d("View: TODO: showCandidates:$candidates")
        logic.updateCandidates(candidates)
        setCandidatesViewShown(true)
        //wordListAdapter.update(cand)
    }

    override fun confirmInput(word: String) {
        log.d("View: confirmInput:$word")
        // XXX Is inserting a space here a right place?
        currentInputConnection.commitText(" $word", 1)
        setCandidatesViewShown(false)
        logic.clearCandidates()
        //findViewById<EditText>(R.id.editText).append(" $word")
        //wordListAdapter.clear()
    }

    fun onBtnClick(view: View) {
        log.d("onBtnClick() btn=" + (view as Button).text)
    }
}