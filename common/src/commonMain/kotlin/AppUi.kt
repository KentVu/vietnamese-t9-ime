import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.ui.UIState
import com.github.kentvu.t9vietnamese.ui.theme.T9VietnameseTheme

@Composable
fun AppUi(uiState: State<UIState>, onKeyClick: (key: Key) -> Unit) {
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
                CandidatesView(uiState.value.candidates)
                Keypad(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    uiState.value.initialized,
                    onKeyClick
                )
            }
        }
    }
}

@Composable
fun Keypad(modifier: Modifier = Modifier, keysEnabled: Boolean, onKeyClick: (key: Key) -> Unit) {
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
            with(VNKeys) {
                KeyboardRow(onKeyClick, keysEnabled, key1, key2, key3)
                KeyboardRow(onKeyClick, keysEnabled, key4, key5, key6)
                KeyboardRow(onKeyClick, keysEnabled, key7, key8, key9)
                KeyboardRow(onKeyClick, keysEnabled, key0)
            }
        }
    }
}

@Composable
private fun CandidatesView(candidates: Set<String>) {
    LazyRow(
        //horizontalArrangement = Arrangement.SpaceBetween,
        //contentPadding = PaddingValues(start = 8.dp),
        modifier = Modifier.semantics {
            contentDescription = Semantic.candidates
        }) {
        //var isFirst = true
        candidates.forEach {
            item(it) {
                Text(it, Modifier.padding(start = if(/*!isFirst*/true) 4.dp else 0.dp))
                //isFirst = false
            }
        }
    }
}

object Semantic {
    const val candidates = "Candidates"
}

@Composable
private fun KeyboardRow(onKeyClick: (key: Key) -> Unit, keysEnabled: Boolean, vararg keys: Key) {
    Row {
        val mod = Modifier
            .padding(1.dp)
            .weight(1F)
        for (key in keys) {
            ComposableKey(key, mod, keysEnabled, onKeyClick)
        }
    }
}

@Composable
private fun ComposableKey(
    key: Key,
    modifier: Modifier,
    keysEnabled: Boolean,
    onKeyClick: (key: Key) -> Unit
) {
    Button(
        modifier = modifier/*.semantics { text = buildAnnotatedString { append(key.symbol) } }*/,
        enabled = keysEnabled,
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
