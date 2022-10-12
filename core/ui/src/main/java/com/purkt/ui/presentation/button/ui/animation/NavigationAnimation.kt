@file:OptIn(ExperimentalAnimationApi::class)

package com.purkt.ui.presentation.button.ui.animation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

fun AnimatedContentScope<NavBackStackEntry>.slideLeftEnter(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentScope.SlideDirection.Left,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
}

fun AnimatedContentScope<NavBackStackEntry>.slideLeftExit(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentScope.SlideDirection.Left,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
}

fun AnimatedContentScope<NavBackStackEntry>.slideRightEnter(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentScope.SlideDirection.Right,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
}

fun AnimatedContentScope<NavBackStackEntry>.slideRightExit(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentScope.SlideDirection.Right,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
}