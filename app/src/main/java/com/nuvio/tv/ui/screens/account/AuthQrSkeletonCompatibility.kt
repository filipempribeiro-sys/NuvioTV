package com.nuvio.tv.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

/**
 * Modifier-based skeleton overload used by the MEDIA•HUB QR card.
 *
 * The inherited Nuvio skeleton helpers are dimension-based. Keeping this
 * small overload local to the account package lets the QR placeholder fill
 * its parent without changing the legacy helpers or the QR layout.
 */
@Composable
internal fun SkeletonBar(
    modifier: Modifier,
    brush: Brush
) {
    Box(modifier = modifier.background(brush))
}
