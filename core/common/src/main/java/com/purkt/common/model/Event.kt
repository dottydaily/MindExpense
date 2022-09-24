package com.purkt.common.model

class Event<T>(private val content: T) {
    var hasBeenRead = false; private set

    fun getContentIfNotRead(): T? {
        return if (hasBeenRead) {
            null
        } else {
            hasBeenRead = true
            content
        }
    }

    fun peekContent(): T = content
}
