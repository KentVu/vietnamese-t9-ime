package com.github.kentvu.t9vietnamese.web

import org.w3c.dom.HTMLElement
import kotlin.test.assertNotNull

class BrowserAppRunner(private val root: HTMLElement) {
  fun showsReportButton(enabled: Boolean) {
    console.log(root)
    val qRes = root.querySelector("[data-testid='${BrowserUI.Semantic.ShowReportUiButton}']")
    assertNotNull(
      qRes
    )
  }

}
