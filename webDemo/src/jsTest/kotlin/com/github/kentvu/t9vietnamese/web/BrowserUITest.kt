package com.github.kentvu.t9vietnamese.web

import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.tests.TestPresenter
import kotlin.collections.last
import kotlin.test.Test
import kotlin.test.assertEquals

class BrowserUITest {
  @Test
  fun `send onStartInputView properly`() {
    val presenter = TestPresenter()
    val ui = BrowserUI(presenter)
    val info = ::EditorInfo
    val ev = KeypadEvent::InputViewStart

    for(mode in setOf(EditorInfo.Class.Normal, EditorInfo.Class.Number)) {
      ui.onStartInputView(info(mode))
      assertEquals(ev(info(mode)), presenter.evHistory.last())
    }
  }
  /*@Test
  fun thingsShouldWork() {
    assertEquals(listOf(1,2,3).reversed(), listOf(3,2,1))
  }

  @Test
  fun thingsShouldBreak() {
    assertEquals(listOf(1,2,3).reversed(), listOf(1,2,3))
  }*/
}
