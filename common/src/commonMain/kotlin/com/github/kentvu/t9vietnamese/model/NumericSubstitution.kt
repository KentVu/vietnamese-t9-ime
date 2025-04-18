package com.github.kentvu.t9vietnamese.model

sealed interface NumericSubstitution {
    fun forNum(n: Char): String

    data object VN : NumericSubstitution {
        override fun forNum(n: Char): String {
            return when(n) {
                '0' -> (" ")
                '1' -> (".,?!")
                '2' -> ("aăâbć")
                '3' -> ("dđef̀ê")
                '4' -> ("ghỉ")
                '5' -> ("jkl̃")
                '6' -> ("mnọôơ")
                '7' -> ("pqrs")
                '8' -> ("tuưv")
                '9' -> ("wxyz")

                else -> error("Not a number($n)!!")
            }
        }
    }
}