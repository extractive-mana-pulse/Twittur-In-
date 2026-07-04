package com.example.twitturin.profile.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil.compose.AsyncImage
import com.example.twitturin.profile.presentation.sealed.EditUser
import com.example.twitturin.profile.presentation.sealed.UserCredentials
import com.example.twitturin.profile.presentation.vm.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onSave: (fullName: String, username: String, email: String, bio: String, country: String, birthday: String) -> Unit,
    onSaved: () -> Unit
) {
    val credentials by viewModel.getUserCredentials.collectAsStateWithLifecycle()
    val editResult by viewModel.editUserResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val user = (credentials as? UserCredentials.Success)?.user

    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var birthday by rememberSaveable { mutableStateOf("") }
    var prefilled by rememberSaveable { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(user) {
        if (user != null && !prefilled) {
            fullName = user.fullName.orEmpty()
            username = user.username.orEmpty()
            bio = user.bio.orEmpty()
            country = user.country.orEmpty()
            birthday = user.birthday.orEmpty()
            prefilled = true
        }
    }

    LaunchedEffect(editResult, saved) {
        if (!saved) return@LaunchedEffect
        when (val current = editResult) {
            is EditUser.Success -> onSaved()
            is EditUser.Error -> {
                snackbarHostState.showSnackbar(current.error)
                saved = false
            }
            is EditUser.Loading -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        saved = true
                        onSave(fullName, username, email, bio, country, birthday)
                    }) {
                        Text(text = "Save")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = user?.profilePicture,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onPickImage)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullName, onValueChange = { fullName = it }, singleLine = true,
                label = { Text(text = "Full name") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username, onValueChange = { username = it }, singleLine = true,
                label = { Text(text = "Username") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email, onValueChange = { email = it }, singleLine = true,
                label = { Text(text = "Email") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = country, onValueChange = { country = it }, singleLine = true,
                label = { Text(text = "Country") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = birthday, onValueChange = { birthday = it }, singleLine = true,
                label = { Text(text = "Birthday") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = bio, onValueChange = { bio = it }, minLines = 3,
                label = { Text(text = "Bio") }
            )
        }
    }
}
