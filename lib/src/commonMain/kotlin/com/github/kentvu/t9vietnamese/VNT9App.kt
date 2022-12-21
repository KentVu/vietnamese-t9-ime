package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.*
import okio.FileSystem

class VNT9App(
    wordlist: WordList,
    fileSystem: FileSystem
) : T9App
by GenericT9App(
    KeyPad(VNKeys.all),
    wordlist,
    fileSystem
)
