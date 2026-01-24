package com.hashsoft.audiotape.logic

class TextHelper {
    companion object {
        fun joinNonEmpty(separator: String, vararg elements: String?): String {
            return elements.filterNot { it.isNullOrEmpty() }
                .joinToString(separator)
        }
    }
}

