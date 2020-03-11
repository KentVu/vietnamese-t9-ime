package com.vutrankien.t9vietnamese.android;

import android.content.Context;
import android.inputmethodservice.Keyboard;

/**
 * Created by vutrankien on 17/07/24.
 */

class T9Keyboard extends Keyboard {
    T9Keyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

}
