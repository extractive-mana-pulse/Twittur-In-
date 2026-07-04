package com.example.twitturin.profile.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel

@Composable
fun ShareProfileBottomSheetScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    userId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    val credentials by viewModel.getUserCredentials.collectAsStateWithLifecycle()

    LaunchedEffect(userId) { viewModel.getUserCredentials(userId) }

    val user = (credentials as? UserCredentials.Success)?.user

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = user?.profilePicture,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user?.fullName.orEmpty(), style = MaterialTheme.typography.titleMedium)
            Text(
                text = "@${user?.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            listOfNotNull(user?.kind, user?.studentId).forEach {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ShareActionRow(icon = Icons.Filled.Share, label = "Share", onClick = onShare)
        ShareActionRow(icon = Icons.Filled.ContentCopy, label = "Copy link", onClick = onCopy)
    }
}

@Composable
private fun ShareActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Text(text = label, modifier = Modifier.padding(start = 16.dp))
    }
}
