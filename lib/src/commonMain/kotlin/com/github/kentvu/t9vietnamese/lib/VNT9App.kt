package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.model.WordList
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