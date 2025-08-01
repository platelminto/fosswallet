package nz.eloque.foss_wallet.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun SwipeToDismiss(
    onRightSwipe: () -> Unit,
    onLeftSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    allowRightSwipe: Boolean = true,
    allowLeftSwipe: Boolean = true,
    leftSwipeIcon: ImageVector = Icons.Default.Edit,
    rightSwipeIcon: ImageVector = Icons.Default.Delete,
    content: @Composable () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val swipeState = rememberSwipeToDismissBoxState()

    val (icon, alignment) = when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Pair(leftSwipeIcon, Alignment.CenterEnd)
        SwipeToDismissBoxValue.StartToEnd -> Pair(rightSwipeIcon, Alignment.CenterStart)
        SwipeToDismissBoxValue.Settled -> Pair(Icons.Default.Delete, Alignment.CenterEnd)
    }

    SwipeToDismissBox(
        modifier = Modifier.animateContentSize(),
        state = swipeState,
        enableDismissFromStartToEnd = allowRightSwipe,
        enableDismissFromEndToStart = allowLeftSwipe,
        backgroundContent = {
            Box(
                contentAlignment = alignment,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.minimumInteractiveComponentSize().alpha(0.5f),
                )
            }
        }
    ) {
        Box(
            modifier = modifier
        ) {
            content()
        }
    }

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            LaunchedEffect(swipeState) {
                onLeftSwipe()
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
            }
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(swipeState) {
                onRightSwipe()
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
            }
        }

        SwipeToDismissBoxValue.Settled -> {
        }
    }
}