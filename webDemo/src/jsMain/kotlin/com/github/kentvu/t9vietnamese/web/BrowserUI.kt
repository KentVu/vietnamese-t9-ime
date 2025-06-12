package com.github.kentvu.t9vietnamese.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.model.Key
import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import kotlin.collections.forEachIndexed
import kotlin.text.orEmpty

class BrowserUI(
  presenter: Presenter,
) : UI {
  private val stateSource = presenter.stateSource
  //private val _ic = presenter._ic

  @Composable
  fun Emulator(confirmedText: String) {
    val state by stateSource.collectAsState()
    // Call onStartInputView once to init the keypad (mimicking IM service)
    LaunchedEffect(state.keypadEventSink) {
      onStartInputView(EditorInfo(EditorInfo.Class.Normal))
    }
    Div({ classes("emulator") }) {
      Div({ classes("screen") }) {
        Span({ classes("prev-text") }) {
          Text(confirmedText)
        }
        Span({ classes("current-text") }) {
          if(state.candidates.isNotEmpty())
            Text(state.candidates.selectedCandidate.text)
        }
      }
      val onKeyClick: (key: Key, isLong: Boolean) -> Unit = { key, isLong ->
        state.keypadEventSink(
          KeypadEvent.KeyPress(
            if (!isLong) key.action
            else key.longAction!!)
        )
      }
      Div({ classes("controller") }) {
        Button({
          onClick { onKeyClick(state.keyPad.keyHash, false) }
          classes("prediction-cycle", "btn", "btn-primary")
        }) {
          I({ classes("bi", "bi-arrow-clockwise") })
          Text(" cycle")
        }
        Button({
          onClick { onKeyClick(state.keyPad.keyBackspace, false) }
          classes("delete", "float-end", "btn", "btn-primary")
        }) {
          I({ classes("bi", "bi-arrow-left-circle") })
          Text(" delete")
        }
      }
      Div({ classes("candidates") }) {
        Ul({ classes("list-group", "list-group-horizontal") }) {
          val candidates = state.candidates
          if (candidates.isEmpty())
            Li({classes("list-group-item", "disabled")}){
              Text("Please type...")
            }
          candidates.forEachIndexed { i, candidate ->
            A("#", {
              onClick {
                state.keypadEventSink(KeypadEvent.CandidateSelect(i))
              }
              classes(buildList {
                add("list-group-item")
                add("list-group-item-action")
                if (candidates.selectedCandidate == candidate) {
                  add("active")
                }
              }) 
              if (candidates.selectedCandidate == candidate) ref { htmlElm ->
                htmlElm.scrollIntoView()
                onDispose {  }
              }
            }) {
              Text(candidate.text)
            }
          }
        }
      }
      if(!state.initialized)
        Div({ classes("loading", "justify-content-center") }) {
          Div({ classes("spinner-border"); attr("role", "status") }) {
            Span({ classes("visually-hidden") }) { Text("Loading...") }
          }
        }
      else Div({ classes("keypad") }) {
        val keypad = state.keyPad
        with(keypad) {
          @Composable
          fun Key(key: Key) = Key(key, onKeyClick)
          Key(key1)
          Key(key2)
          Key(key3)
          Key(key4)
          Key(key5)
          Key(key6)
          Key(key7)
          Key(key8)
          Key(key9)
          //Key(key0)
          Key(com.github.kentvu.t9vietnamese.model.Key(Action.Zero))
          /*Button({
            onClick{ onKeyClick(key0, true) }
            classes("key", "key-0")
            value("0")
          }) {
            Text("0")
          }*/
          Button({
            onClick{ onKeyClick(keyStar, false) }
            classes("key", "key-star")
            value("symbol")
          }) {
            Text("*")
          }
          Button({
            onClick{ onKeyClick(key0, false) }
            //onLongClick {}
            classes("key", "key-space")
            value("0")
          }) {
            Text("${key0.action.displaySymbol}")
            Small { Text("${key0.longAction?.displaySymbol.orEmpty()}") }
          }
        }
      }
    }
  }

  @Composable
  private fun Key(
    key: Key,
    onKeyClick: (key: Key, isLong: Boolean) -> Unit,
  ) {
    val displaySymbol = key.action.displaySymbol
    Button({
      onClick { onKeyClick(key, false) }
      classes("key", "key-$displaySymbol")
      value(displaySymbol + key.longAction?.let { " $it" })
    }) {
      Text(displaySymbol)
      Small { Text(key.subChars.orEmpty()) }
    }
  }

  companion object {
    private val log = Logger.tag("WebUI")
  }

  override fun onStartInputView(info: EditorInfo): Boolean {
    stateSource.value.keypadEventSink(KeypadEvent.InputViewStart(
      EditorInfo(EditorInfo.Class.Normal)))
    return true
  }
}
