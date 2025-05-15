package com.github.kentvu.t9vietnamese.web

import androidx.compose.runtime.Composable
import com.github.kentvu.t9vietnamese.FakeInputConnection
import com.github.kentvu.t9vietnamese.Presenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

class WebPresenter(
  //private val scope: CoroutineScope = MainScope(),
  private val stateSource: MutableStateFlow<Presenter.State> = MutableStateFlow(Presenter.State {}),
) : Presenter {
  override val inputConnection = FakeInputConnection()

  override fun updateState(manipulator: (Presenter.State) -> Presenter.State) {
    this.stateSource.update { manipulator(it) }
  }

  @Composable
  fun Emulator() {
    Div({ classes("emulator") }) {
      Div({ classes("screen") }) {
        Span({ classes("prev-text") }); Span({ classes("current-text") })
      }
      Div({ classes("controller") }) {
        Button({ classes("prediction-cycle", "btn", "btn-primary") }) {
          I({ classes("bi", "bi-arrow-clockwise") })
          Text(" cycle")
        }
        Button({ classes("delete", "float-end", "btn", "btn-primary") }) {
          I({ classes("bi", "bi-arrow-left-circle") })
          Text(" delete")
        }
      }
      Div({ classes("keypad") }) {
        Key()
        Button({ classes("key", "key-2"); value("2") }) {
          Text("2 ")
          Small { Text("abc") }
        }
        Button({ classes("key", "key-3"); value("3") }) {
          Text("3")
        }
        Button({ classes("key", "key-4"); value("4") }) {
          Text("4")
        }
        Button({ classes("key", "key-5"); value("5") }) {
          Text("5")
        }
        Button({ classes("key", "key-6"); value("6") }) {
          Text("6")
        }
        Button({ classes("key", "key-7"); value("7") }) {
          Text("7")
        }
        Button({ classes("key", "key-8"); value("8") }) {
          Text("8")
        }
        Button({ classes("key", "key-9"); value("9") }) {
          Text("9")
        }
        Button({ classes("key", "key-star"); value("symbol") }) {
          Text("* #")
        }
        Button({ classes("key", "key-0"); value("0") }) {
          Text("0")
        }
        Button({ classes("key", "key-space"); value("space") }) {
          Small { Text("space") }
        }
      }
    }
  }

  @Composable
  private fun Key() {
    Button({ classes("key", "key-1"); value("1") }) {
      Text("1")
    }
  }
}