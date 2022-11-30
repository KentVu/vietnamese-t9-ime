package com.github.kentvu.t9vietnamese.android

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var candidates by remember { mutableStateOf(listOf<String>()) }
            App()
        }
    }
}

@Preview
@Composable
fun PreviewKeyboard() {
    App()
}
