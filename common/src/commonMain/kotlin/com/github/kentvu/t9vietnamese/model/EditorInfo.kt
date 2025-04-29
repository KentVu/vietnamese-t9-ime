package com.github.kentvu.t9vietnamese.model

data class EditorInfo(val aClass: Class) {

    enum class Class {
        Normal,
        Number
    }
    companion object

}
