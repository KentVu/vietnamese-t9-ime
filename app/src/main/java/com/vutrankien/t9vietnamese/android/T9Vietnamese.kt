package com.vutrankien.t9vietnamese.android

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.vutrankien.t9vietnamese.lib.LogFactory
import javax.inject.Inject

/**
 * Created by vutrankien on 17/05/02.
 */
class T9Vietnamese : InputMethodService() {
    @Inject
    lateinit var logFactory: LogFactory
    private lateinit var log: LogFactory.Log

    override fun onCreate() {
        super.onCreate()
        (application as T9Application).appComponent.inject(this)
        log = logFactory.newLog("T9IMService")
        log.d("onCreate")
    }

    override fun onCreateInputView(): View {
        val inputView = layoutInflater.inflate(
            R.layout.input, null) as (T9KeyboardView)
        inputView.keyboard = T9Keyboard(this, R.xml.t9);
        return inputView
        //return layoutInflater.inflate(
        //    R.layout.dialpad_table_old, null
        //) as ConstraintLayout
    }

    fun onBtnClick(view: View) {
        log.d("onBtnClick() btn=" + (view as Button).text)
    }
}