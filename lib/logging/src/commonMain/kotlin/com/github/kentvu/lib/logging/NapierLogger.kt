package com.github.kentvu.lib.logging

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class NapierLogger(private val tag: String) : Logger {
  //private val napier = Napier.tag(s)

  override fun debug(message: () -> String) {
    Napier.d(tag = tag, message = message)
  }

  override fun debug(message: String) {
    Napier.d(message, tag = tag)
  }

  override fun info(message: String) {
    Napier.i(message, tag = tag)
  }

  override fun warn(e: Throwable, message: String) {
    Napier.w(message, e, tag)
  }

  override fun warn(message: String) {
    Napier.w(message, tag = tag)
  }

  override fun error(e: Throwable, message: String) {
    Napier.e(message, e, tag)
  }

  override fun error(message: String) {
    Napier.e(message, tag = tag)
  }

  companion object {
    private var initialized = false
    fun init() {
      if (!initialized) {
        Napier.base(DebugAntilog())
        initialized = true
      }
    }
  }
}
