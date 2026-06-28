package com.example.twitturin.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditProfileRoot(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ProfileEvent.Saved -> onSaved()
            is ProfileEvent.ShowError -> pendingError = event.message
            ProfileEvent.LoggedOut, ProfileEvent.AccountDeleted -> Unit
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    EditProfileScreen(
        state = state,
        onBack = onBack,
        onSave = viewModel::editProfile,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    state: ProfileState,
    onBack: () -> Unit,
    onSave: (fullName: String, username: String, email: String, bio: String, country: String, birthday: String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val user = state.user

    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var birthday by rememberSaveable { mutableStateOf("") }
    var prefilled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(user) {
        if (user != null && !prefilled) {
            fullName = user.fullName
            username = user.username
            email = user.email
            bio = user.bio
            country = user.country
            birthday = user.birthday
            prefilled = true
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit profile") },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "Back") } },
                actions = {
                    TextButton(
                        enabled = !state.isLoading,
                        onClick = { onSave(fullName, username, email, bio, country, birthday) },
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(text = "Save")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Image picking is a separate platform (expect/actual) task — avatar is read-only for now.
            AsyncImage(
                model = user?.profilePicture,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullName, onValueChange = { fullName = it }, singleLine = true,
                label = { Text(text = "Full name") },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username, onValueChange = { username = it }, singleLine = true,
                label = { Text(text = "Username") },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email, onValueChange = { email = it }, singleLine = true,
                label = { Text(text = "Email") },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = country, onValueChange = { country = it }, singleLine = true,
                label = { Text(text = "Country") },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = birthday, onValueChange = { birthday = it }, singleLine = true,
                label = { Text(text = "Birthday") },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = bio, onValueChange = { bio = it }, minLines = 3,
                label = { Text(text = "Bio") },
            )
        }
    }
}
