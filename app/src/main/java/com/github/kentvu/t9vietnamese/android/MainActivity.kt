package com.github.kentvu.t9vietnamese.android

import AppUi
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.github.kentvu.t9vietnamese.lib.VNT9App
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.ForwardingFileSystem
import okio.source

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = VNT9App(
            VietnameseWordList,
            AndroidFileSystem(applicationContext)
        )
        setContent {
            var candidates by remember { mutableStateOf(listOf<String>()) }
            var appInitialized by remember { mutableStateOf(false) }
            LaunchedEffect(1) {
                withContext(Dispatchers.IO) {
                    app.init()
                }
                appInitialized = true
            }
            AppUi(appInitialized) {
                app.type(it)
                Log.d("MainActivity", "${it.symbol}: ${app.candidates}")
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
