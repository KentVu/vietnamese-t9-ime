package com.github.kentvu.lib.logging

/** Like [TODO] but only logging. */
fun LogTODO(reason: String = "Not yet implemented") {
  try {
    TODO(reason)
  } catch (e: NotImplementedError) {
    Logger.tag("!FLCC!").error(e)
  }
}
