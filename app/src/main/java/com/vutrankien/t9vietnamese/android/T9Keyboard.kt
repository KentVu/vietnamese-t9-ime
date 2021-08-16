package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.inputmethodservice.Keyboard

/**
 * Created by vutrankien on 17/07/24.
 */
internal class T9Keyboard @JvmOverloads constructor(context: Context?, xmlLayoutResId: Int = R.xml.t9) : Keyboard(context, xmlLayoutResId)