package com.github.kentvu.t9vietnamese.lib

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Update by assuming `this` is a [MutableStateFlow].
 * @return false if newValue is the same as old value. */
@OptIn(ExperimentalContracts::class)
inline fun <T> StateFlow<T>.update(newProvider: T.() -> T): Boolean {
  contract {
    callsInPlace(newProvider, InvocationKind.EXACTLY_ONCE)
  }
  (this as MutableStateFlow<T>)
  val newValue = newProvider(value)
  if (newValue == value) return false
  value = newValue
  return true
}

