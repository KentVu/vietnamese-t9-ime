package com.github.kentvu.t9vietnamese.logging

import com.github.kentvu.t9vietnamese.logging.Logger

/** Like [TODO] but only logging. */
fun LogTODO(reason: String = "Not yet implemented") {
  try {
    TODO(reason)
  } catch (e: NotImplementedError) {
    Logger.tag("!FLCC!").error(e)
  }
}
