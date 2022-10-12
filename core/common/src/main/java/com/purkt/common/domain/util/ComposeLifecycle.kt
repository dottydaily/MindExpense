package com.purkt.common.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

object ComposeLifecycle {
    @Composable
    fun DoOnLifecycle(
        lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
        onCreate: (() -> Unit)? = null,
        onStart: (() -> Unit)? = null,
        onResume: (() -> Unit)? = null,
        onPause: (() -> Unit)? = null,
        onStop: (() -> Unit)? = null,
        onDestroy: (() -> Unit)? = null,
        onAny: (() -> Unit)? = null
    ) {
        val currentOnCreate = rememberUpdatedState(newValue = onCreate)
        val currentOnStart = rememberUpdatedState(newValue = onStart)
        val currentOnResume = rememberUpdatedState(newValue = onResume)
        val currentOnPause = rememberUpdatedState(newValue = onPause)
        val currentOnStop = rememberUpdatedState(newValue = onStop)
        val currentOnDestroy = rememberUpdatedState(newValue = onDestroy)
        val currentOnAny = rememberUpdatedState(newValue = onAny)
        DisposableEffect(key1 = lifecycleOwner) {
            val observer = LifecycleEventObserver { owner, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> currentOnCreate.value?.invoke()
                    Lifecycle.Event.ON_START -> currentOnStart.value?.invoke()
                    Lifecycle.Event.ON_RESUME -> currentOnResume.value?.invoke()
                    Lifecycle.Event.ON_PAUSE -> currentOnPause.value?.invoke()
                    Lifecycle.Event.ON_STOP -> currentOnStop.value?.invoke()
                    Lifecycle.Event.ON_DESTROY -> currentOnDestroy.value?.invoke()
                    Lifecycle.Event.ON_ANY -> currentOnAny.value?.invoke()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }
    }
}
