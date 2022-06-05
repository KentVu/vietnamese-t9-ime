package com.github.kentvu.t9vietnamese.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.kentvu.t9vietnamese.android.view.MainActivityView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityView()
        }

    }

    @Preview
    @Composable
    fun PreviewKeyboard() {
        MainActivityView()
    }
}