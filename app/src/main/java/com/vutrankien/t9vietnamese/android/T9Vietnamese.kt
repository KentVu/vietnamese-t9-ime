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
    }

    override fun onCreateInputView(): View {
        //T9KeyboardView inputView = (T9KeyboardView) getLayoutInflater().inflate(
//        R.layout.input, null);
//inputView.setKeyboard(new T9Keyboard(this, R.xml.t9));
        return layoutInflater.inflate(
            R.layout.dialpad_table, null
        ) as ConstraintLayout
    }

    fun onBtnClick(view: View) {
        log.d("onBtnClick() btn=" + (view as Button).text)
    }
}