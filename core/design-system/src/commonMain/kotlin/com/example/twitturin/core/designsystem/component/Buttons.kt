package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.designsystem.theme.OnBrand

/** Filled pill CTA — height 52, fully rounded, brand fill. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Brand, contentColor = OnBrand),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(color = OnBrand, strokeWidth = 1.5.dp)
        } else {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.padding(end = 8.dp).height(20.dp))
            }
            Text(text)
        }
    }
}

/** Outlined CTA — height 52, 16dp corners, brand outline + text. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Brand),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Brand),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.padding(end = 8.dp).height(20.dp))
        }
        Text(text)
    }
}

/** Destructive outlined CTA — danger outline + text. */
@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.5.dp, Danger),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
    ) {
        Text(text)
    }
}

/** Compact follow / unfollow pill. following=false → filled "Follow"; true → outline "Unfollow". */
@Composable
fun FollowButton(
    following: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    followText: String = "Follow",
    followingText: String = "Unfollow",
) {
    val shape = RoundedCornerShape(20.dp)
    if (following) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.heightIn(min = 38.dp),
            shape = shape,
            border = BorderStroke(1.5.dp, Brand),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Brand),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        ) { Text(followingText) }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.heightIn(min = 38.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(containerColor = Brand, contentColor = OnBrand),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        ) { Text(followText) }
    }
}

/** A row of inline text actions for dialogs (e.g. "No" / "Yes"). */
@Composable
internal fun DialogActions(
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}
