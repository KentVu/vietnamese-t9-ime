package com.github.kentvu.t9vietnamese.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.kentvu.t9vietnamese.android.ui.theme.T9VietnameseTheme
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeysCollection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var candidates by remember { mutableStateOf(listOf<String>()) }
            MainActivityView()
        }
    }

    @Composable
    private fun MainActivityView() {
        T9VietnameseTheme {
            Scaffold(topBar = {
                TopAppBar(title = {
                    Text("T9Vietnamese")
                })
            }) { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CandidatesView()
                    Keypad(Modifier.padding(innerPadding)) {
                        Log.d(
                            "MainActivity",
                            "KeyClicked: $it"
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Keypad(modifier: Modifier = Modifier, onKeyClick: (key: Key) -> Unit) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            //color = MaterialTheme.colors.secondary,
            elevation = 1.dp,
            modifier = modifier
                .animateContentSize()
                .padding(1.dp)
        ) {
            Column {
                KeyboardRow(KeysCollection.key1, KeysCollection.key2, KeysCollection.key3, onKeyClick)
                KeyboardRow(KeysCollection.key4, KeysCollection.key5, KeysCollection.key6, onKeyClick)
                KeyboardRow(KeysCollection.key7, KeysCollection.key8, KeysCollection.key9, onKeyClick)
            }
        }
    }

    @Composable
    private fun CandidatesView() {
        LazyRow {

        }
    }

    @Composable
    private fun KeyboardRow(key1: Key, key2: Key, key3: Key, onKeyClick: (key: Key) -> Unit) {
        Row {
            val mod = Modifier
                .padding(1.dp)
                .weight(1F)
            ComposableKey(key1, mod, onKeyClick)
            ComposableKey(key2, mod, onKeyClick)
            ComposableKey(key3, mod, onKeyClick)
        }
    }

    @Composable
    private fun ComposableKey(key: Key, modifier: Modifier, onKeyClick: (key: Key) -> Unit) {
        Button(modifier = modifier, onClick = { onKeyClick(key) }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${key.symbol}",
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    key.subtext,
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
    }
    @Preview
    @Composable
    fun PreviewKeyboard() {
        MainActivityView()
    }
}