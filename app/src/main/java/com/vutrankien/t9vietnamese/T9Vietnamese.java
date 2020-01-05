package com.vutrankien.t9vietnamese;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.Button;

/**
 * Created by vutrankien on 17/05/02.
 */
public class T9Vietnamese extends InputMethodService {
    @Override
    public View onCreateInputView() {
        //T9KeyboardView inputView = (T9KeyboardView) getLayoutInflater().inflate(
        //        R.layout.input, null);
        //inputView.setKeyboard(new T9Keyboard(this, R.xml.t9));
        @SuppressLint("InflateParams") @SuppressWarnings("UnnecessaryLocalVariable") ConstraintLayout inputView = (ConstraintLayout) getLayoutInflater().inflate(
                R.layout.dialpad_table, null);
        return inputView;
    }
    @SuppressWarnings("unused")
    public void onBtnClick(View view) {
        new Logging("T9Vietnamese").d("onBtnClick() btn=" + ((Button) view).getText());
    }
}
