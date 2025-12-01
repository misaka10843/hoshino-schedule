package com.misaka.hoshinoschedule.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import android.provider.Settings
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.transitions.ScreenTransition
import cafe.adriel.voyager.transitions.ScreenTransitionContent

/**
 * Mihon-style navigation transitions with fade + slight scale/slide effects.
 * Provides consistent animations for both Compose Navigation and Voyager.
 * Respects accessibility settings by checking AnimatorDurationScale.
 */
object KiraraScreenTransition {
    private const val ANIMATION_DURATION = 300
    private const val FADE_DURATION = 200
    private const val SLIDE_DISTANCE = 100
    private const val SCALE_FACTOR = 0.95f

    /**
     * Check if animations should be enabled based on accessibility settings
     */
    private fun areAnimationsEnabled(context: android.content.Context): Boolean {
        return try {
            val animationScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            animationScale > 0.0f
        } catch (e: Settings.SettingNotFoundException) {
            true // Default to enabled if setting not found
        }
    }

    /**
     * Get animation duration based on accessibility settings
     */
    private fun getAdjustedDuration(context: android.content.Context, baseDuration: Int): Int {
        return if (!areAnimationsEnabled(context)) {
            0 // No animation when disabled
        } else {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            (baseDuration * scale).toInt()
        }
    }

    /**
     * Forward navigation transition (pushing new screen)
     */
    fun forwardEnter(context: android.content.Context? = null): EnterTransition {
        val duration = context?.let { getAdjustedDuration(it, ANIMATION_DURATION) } ?: ANIMATION_DURATION
        val fadeDuration = context?.let { getAdjustedDuration(it, FADE_DURATION) } ?: FADE_DURATION
        
        return if (duration == 0) {
            fadeIn(animationSpec = tween(1))
        } else {
            slideInHorizontally(
                initialOffsetX = { SLIDE_DISTANCE },
                animationSpec = tween(duration, easing = EaseOut)
            ) + fadeIn(
                animationSpec = tween(fadeDuration, easing = EaseOut),
                initialAlpha = 0.3f
            ) + scaleIn(
                initialScale = SCALE_FACTOR,
                animationSpec = tween(duration, easing = EaseOut)
            )
        }
    }

    /**
     * Forward navigation transition (current screen exiting)
     */
    fun forwardExit(context: android.content.Context? = null): ExitTransition {
        val duration = context?.let { getAdjustedDuration(it, ANIMATION_DURATION) } ?: ANIMATION_DURATION
        val fadeDuration = context?.let { getAdjustedDuration(it, FADE_DURATION) } ?: FADE_DURATION
        
        return if (duration == 0) {
            fadeOut(animationSpec = tween(1))
        } else {
            slideOutHorizontally(
                targetOffsetX = { -SLIDE_DISTANCE / 4 },
                animationSpec = tween(duration, easing = EaseIn)
            ) + fadeOut(
                animationSpec = tween(fadeDuration, easing = EaseIn)
            ) + scaleOut(
                targetScale = 1.05f,
                animationSpec = tween(duration, easing = EaseIn)
            )
        }
    }

    /**
     * Back navigation transition (popping current screen)
     */
    fun backEnter(context: android.content.Context? = null): EnterTransition {
        val duration = context?.let { getAdjustedDuration(it, ANIMATION_DURATION) } ?: ANIMATION_DURATION
        val fadeDuration = context?.let { getAdjustedDuration(it, FADE_DURATION) } ?: FADE_DURATION
        
        return if (duration == 0) {
            fadeIn(animationSpec = tween(1))
        } else {
            slideInHorizontally(
                initialOffsetX = { -SLIDE_DISTANCE / 4 },
                animationSpec = tween(duration, easing = EaseOut)
            ) + fadeIn(
                animationSpec = tween(fadeDuration, easing = EaseOut),
                initialAlpha = 0.3f
            ) + scaleIn(
                initialScale = 1.05f,
                animationSpec = tween(duration, easing = EaseOut)
            )
        }
    }

    /**
     * Back navigation transition (previous screen entering)
     */
    fun backExit(context: android.content.Context? = null): ExitTransition {
        val duration = context?.let { getAdjustedDuration(it, ANIMATION_DURATION) } ?: ANIMATION_DURATION
        val fadeDuration = context?.let { getAdjustedDuration(it, FADE_DURATION) } ?: FADE_DURATION
        
        return if (duration == 0) {
            fadeOut(animationSpec = tween(1))
        } else {
            slideOutHorizontally(
                targetOffsetX = { SLIDE_DISTANCE },
                animationSpec = tween(duration, easing = EaseIn)
            ) + fadeOut(
                animationSpec = tween(fadeDuration, easing = EaseIn)
            ) + scaleOut(
                targetScale = SCALE_FACTOR,
                animationSpec = tween(duration, easing = EaseIn)
            )
        }
    }

    /**
     * Creates a Voyager ScreenTransition with Mihon-style animations
     */
    @Composable
    fun DefaultNavigatorScreenTransition(
        navigator: cafe.adriel.voyager.navigator.Navigator,
        content: @Composable ScreenTransitionContent
    ) {
        val context = LocalContext.current
        ScreenTransition(
            navigator = navigator,
            transition = { screen ->
                // Determine if this is a forward or backward navigation
                val isForward = navigator.items.indexOf(screen) > navigator.items.indexOf(navigator.lastItem)
                val duration = getAdjustedDuration(context, ANIMATION_DURATION)
                
                if (duration == 0) {
                    // No animations when disabled
                    fadeIn(animationSpec = tween(1)) with fadeOut(animationSpec = tween(1))
                } else if (isForward) {
                    slideInHorizontally(
                        initialOffsetX = { SLIDE_DISTANCE },
                        animationSpec = tween(duration, easing = EaseOut)
                    ) with slideOutHorizontally(
                        targetOffsetX = { -SLIDE_DISTANCE / 4 },
                        animationSpec = tween(duration, easing = EaseIn)
                    )
                } else {
                    slideInHorizontally(
                        initialOffsetX = { -SLIDE_DISTANCE / 4 },
                        animationSpec = tween(duration, easing = EaseOut)
                    ) with slideOutHorizontally(
                        targetOffsetX = { SLIDE_DISTANCE },
                        animationSpec = tween(duration, easing = EaseIn)
                    )
                }
            }
        ) {
            content(it)
        }
    }
}

/**
 * Extension functions for AnimatedContentTransitionScope to easily apply transitions
 */
fun AnimatedContentTransitionScope<*>.kiraraForwardEnter(): EnterTransition = 
    KiraraScreenTransition.forwardEnter()

fun AnimatedContentTransitionScope<*>.kiraraForwardExit(): ExitTransition = 
    KiraraScreenTransition.forwardExit()

fun AnimatedContentTransitionScope<*>.kiraraBackEnter(): EnterTransition = 
    KiraraScreenTransition.backEnter()

fun AnimatedContentTransitionScope<*>.kiraraBackExit(): ExitTransition = 
    KiraraScreenTransition.backExit()