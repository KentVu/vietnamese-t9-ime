import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kentvu.t9vietnamese.ui.theme.T9VietnameseTheme
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeysCollection

@Composable
fun AppUi() {
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
                Keypad(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    //Log.d("MainActivity", "KeyClicked: $it")
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
        Column(verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,) {
            KeyboardRow(onKeyClick, KeysCollection.key1, KeysCollection.key2, KeysCollection.key3)
            KeyboardRow(onKeyClick, KeysCollection.key4, KeysCollection.key5, KeysCollection.key6)
            KeyboardRow(onKeyClick, KeysCollection.key7, KeysCollection.key8, KeysCollection.key9)
            KeyboardRow(onKeyClick, KeysCollection.key0)
        }
    }
}

@Composable
private fun CandidatesView() {
    LazyRow {

    }
}

@Composable
private fun KeyboardRow(onKeyClick: (key: Key) -> Unit, vararg keys: Key) {
    Row {
        val mod = Modifier
            .padding(1.dp)
            .weight(1F)
        for (key in keys) {
            ComposableKey(key, mod, onKeyClick)
        }
    }
}

@Composable
private fun ComposableKey(key: Key, modifier: Modifier, onKeyClick: (key: Key) -> Unit) {
    Button(
        modifier = modifier/*.semantics { text = buildAnnotatedString { append(key.symbol) } }*/,
        onClick = { onKeyClick(key) }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${key.symbol}",
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                key.subChars,
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

expect fun getPlatformName(): String