package com.github.kentvu.t9vietnamese.web

import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeyPad
import kotlinx.browser.document
import org.w3c.dom.events.KeyboardEvent

fun KeyPad.keyboardMap(): Map<String, Key> {
  return mapOf(
    "q" to key1,
    "w" to key2,
    "e" to key3,
    "a" to key4,
    "s" to key5,
    "d" to key6,
    "z" to key7,
    "x" to key8,
    "c" to key9,
    "r" to keyStar,
    "f" to key0,
  )
}