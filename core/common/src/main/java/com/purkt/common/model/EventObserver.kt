package com.purkt.common.model

import androidx.lifecycle.Observer

class EventObserver<T>(private val onEventChanged: (T) -> Unit = {}) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.let {
            if (!it.hasBeenRead) {
                it.getContentIfNotRead()?.let { content ->
                    onEventChanged.invoke(content)
                }
            }
        }
    }
}
