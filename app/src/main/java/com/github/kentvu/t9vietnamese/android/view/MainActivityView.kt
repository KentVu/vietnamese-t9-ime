package com.github.kentvu.t9vietnamese.android.view

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.kentvu.t9vietnamese.android.ui.theme.T9VietnameseTheme
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.StandardKeys

@Composable
fun MainActivityView() {
    T9VietnameseTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                Text("T9Vietnamese")
            })
        }) { innerPadding ->
            Keypad(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize())
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
        Column(verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,) {
            KeyboardRow(StandardKeys.key1, StandardKeys.key2, StandardKeys.key3)
            KeyboardRow(StandardKeys.key4, StandardKeys.key5, StandardKeys.key6)
            KeyboardRow(StandardKeys.key7, StandardKeys.key8, StandardKeys.key9)
            KeyboardRow(StandardKeys.key0)
        }
    }
}

@Composable
private fun KeyboardRow(vararg keys: Key) {
    Row(horizontalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxWidth()) {
        val pad = Modifier.padding(1.dp)
        for (key in keys) {
            ComposableKey(key, pad)
        }
    }
}

@Composable
private fun ComposableKey(key: Key, modifier: Modifier) {
    Button(
        modifier = modifier/*.semantics { text = buildAnnotatedString { append(key.symbol) } }*/,
        onClick = { Log.d("MainActivity", "$key clicked") }
    ) {
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
