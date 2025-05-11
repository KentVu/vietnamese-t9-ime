package com.github.kentvu.t9vietnamese.web

import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
  renderComposable(rootElementId = "root") {
    Main {
      Div({
        classes("container", "py-5")
      }) {
        H2({
          classes("pb-2", "px-4", "border-bottom")
        }) {
          Img(src = "ic_t9.svg", attrs = {
            classes("bi")
            attr("width", "32")
            attr("height", "32")
            //style { width(1.em); height(1.em) }
          })
          Text("T9 keypad demo")
        }
        Div({
          classes("row", "g-4", "row-cols-1", "row-cols-lg-3")
        }) {
          Div({
            classes("feature", "col")
          }) {
            Div({
              classes("feature-icon", "d-inline-flex", "align-items-center", "justify-content-center", "text-bg-primary", "bg-gradient", "fs-2", "mb-3")
            }) {
            }
          }
        }
        Div({ classes("row") }) {
          Div({ classes("col-md-4") }) {
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
                Button({ classes("key", "key-1"); value("1") }) {
                  Text("1")
                }
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
        }
      }
    }
  }
}
