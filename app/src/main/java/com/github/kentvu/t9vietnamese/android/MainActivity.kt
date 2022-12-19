package com.github.kentvu.t9vietnamese.android

import AppUi
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.github.kentvu.t9vietnamese.DefaultApp
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.KeysCollection
import okio.FileSystem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = DefaultApp(
            KeyPad(listOf(
                KeysCollection.key1, KeysCollection.key2, KeysCollection.key3,
                KeysCollection.key4, KeysCollection.key5, KeysCollection.key6,
                KeysCollection.key7, KeysCollection.key8, KeysCollection.key9,
                KeysCollection.key0,
            )),
            VietnameseWordList,
            FileSystem.SYSTEM
        )
        setContent {
            var candidates by remember { mutableStateOf(listOf<String>()) }
            AppUi()
        }
    }
}

@Preview
@Composable
fun PreviewKeyboard() {
    AppUi()
}
