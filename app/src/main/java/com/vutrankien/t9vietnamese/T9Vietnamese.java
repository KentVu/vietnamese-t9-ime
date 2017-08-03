package com.vutrankien.t9vietnamese;

import android.inputmethodservice.InputMethodService;
import android.view.View;

/**
 * Created by vutrankien on 17/05/02.
 */
public class T9Vietnamese extends InputMethodService {
    @Override
    public View onCreateInputView() {
        T9KeyboardView inputView = (T9KeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        inputView.setKeyboard(new T9Keyboard(this, R.xml.t9));
        return inputView;
    }
}
