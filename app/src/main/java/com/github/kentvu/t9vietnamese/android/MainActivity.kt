package com.github.kentvu.t9vietnamese.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.kentvu.t9vietnamese.android.ui.theme.T9VietnameseTheme
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.StandardKeys

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                Keypad(Modifier.padding(innerPadding))
            }
        }
    }

    @Composable
    fun Keypad(modifier: Modifier = Modifier) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            //color = MaterialTheme.colors.secondary,
            elevation = 1.dp,
            modifier = modifier
                .animateContentSize()
                .padding(1.dp)
        ) {
            Column {
                KeyboardRow(StandardKeys.key1, StandardKeys.key2, StandardKeys.key3)
                KeyboardRow(StandardKeys.key4, StandardKeys.key5, StandardKeys.key6)
                KeyboardRow(StandardKeys.key7, StandardKeys.key8, StandardKeys.key9)
            }
        }
    }

    @Composable
    private fun KeyboardRow(key1: Key, key2: Key, key3: Key) {
        Row {
            val pad = Modifier.padding(1.dp)
            ComposableKey(key1, pad)
            ComposableKey(key2, pad)
            ComposableKey(key3, pad)
        }
    }

    @Composable
    private fun ComposableKey(key: Key, modifier: Modifier) {
        Button(modifier = modifier, onClick = { /*TODO*/ }) {
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