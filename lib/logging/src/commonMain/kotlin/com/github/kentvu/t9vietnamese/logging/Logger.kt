package com.github.kentvu.t9vietnamese.logging

interface Logger {
  fun debug(message: () -> String)
  fun debug(message: String)
  fun info(message: String/*, throwable: Throwable? = null, tag: String? = null*/)
  fun warn(e: Throwable, message: String = "")
  fun warn(message: String)
  fun error(e: Throwable, message: String = "")
  fun error(message: String)

  companion object {
    fun tag(s: String): Logger {
      return NapierLogger(s)
    }
  }
}