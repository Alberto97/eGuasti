package net.albertopedron.eguasti.ui.components

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

fun bottomSlideInVertically() = slideInVertically(initialOffsetY = { fullHeight -> fullHeight })
fun bottomSlideOutVertically() = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight })