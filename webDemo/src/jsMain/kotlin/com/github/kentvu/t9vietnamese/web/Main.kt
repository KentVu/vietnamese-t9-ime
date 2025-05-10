package com.github.kentvu.t9vietnamese.web

import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        Main {
            Div({
                classes("container", "px-4", "py-5")
            }) {
                H2({
                    classes("pb-2", "border-bottom")
                }) {
                    Text("T9 keypad demo")
                }
                Div({
                    classes("row", "g-4", "py-5", "row-cols-1", "row-cols-lg-3")
                }) {
                    Div({
                        classes("feature", "col")
                    }) {
                        Div({
                            classes("feature-icon", "d-inline-flex", "align-items-center", "justify-content-center", "text-bg-primary", "bg-gradient", "fs-2", "mb-3")
                        }) {
                            Img(src = "ic_t9.svg", attrs = {
                                classes("bi")
                                style { width(1.em); height(1.em) }
                            }
                            )
                        }
                    }
                }
            }
        }
    }
}
