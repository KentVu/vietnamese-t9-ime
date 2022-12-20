package com.github.kentvu.t9vietnamese.android

import AppUi
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.github.kentvu.t9vietnamese.VNT9App
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.source

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = VNT9App(
            DecomposedVietnameseWords(
                DecomposedVietnameseWords::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!.source()
            ),
            FileSystem.SYSTEM
        )
        setContent {
            var candidates by remember { mutableStateOf(listOf<String>()) }
            var appInitialized by remember { mutableStateOf(false) }
            LaunchedEffect(1) {
                withContext(Dispatchers.IO) { app.init() }
                appInitialized = true
            }
            AppUi(appInitialized) {
                app.type(it)
            }
        }
    }
}

@Preview
@Composable
fun PreviewKeyboard() {
    AppUi(true) {

    }
}
