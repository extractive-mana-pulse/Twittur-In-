package com.example.twitturin.auth.presentation.registration.professor.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.registration.professor.sealed.SignUpProfResult
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel,
    onBack: () -> Unit,
    onRegistered: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val result by viewModel.profRegResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val usernameHasSpace = username.contains(" ")
    val isLoading = submitted && result is SignUpProfResult.Loading
    val canSubmit = fullName.isNotBlank() && username.isNotBlank() &&
            subject.isNotBlank() && password.isNotBlank() && !usernameHasSpace && !isLoading

    LaunchedEffect(result, submitted) {
        if (!submitted) return@LaunchedEffect
        when (val current = result) {
            is SignUpProfResult.Success -> onRegistered()
            is SignUpProfResult.Error -> {
                snackbarHostState.showSnackbar(current.message)
                submitted = false
            }
            SignUpProfResult.Loading -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.registration)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullName,
                onValueChange = { fullName = it },
                singleLine = true,
                label = { Text(text = stringResource(R.string.full_name)) },
                leadingIcon = { Icon(painterResource(R.drawable.full_name), contentDescription = null) }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = { if (it.length <= 30) username = it },
                singleLine = true,
                isError = usernameHasSpace,
                label = { Text(text = stringResource(R.string.username)) },
                leadingIcon = { Icon(painterResource(R.drawable.username_icon), contentDescription = null) },
                supportingText = {
                    if (usernameHasSpace) Text(text = stringResource(R.string.no_spaces_allowed))
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = subject,
                onValueChange = { subject = it },
                singleLine = true,
                label = { Text(text = stringResource(R.string.subject)) }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                label = { Text(text = stringResource(R.string.password)) },
                leadingIcon = { Icon(painterResource(R.drawable.lock), contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canSubmit,
                onClick = {
                    submitted = true
                    viewModel.signUpProf(
                        fullName.trim(),
                        username.trim(),
                        subject.trim(),
                        password.trim(),
                        "teacher"
                    )
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = stringResource(R.string.sign_up))
                }
            }
        }
    }
}
