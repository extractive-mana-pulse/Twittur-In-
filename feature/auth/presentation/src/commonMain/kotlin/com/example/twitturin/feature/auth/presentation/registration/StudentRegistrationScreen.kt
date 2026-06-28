package com.example.twitturin.feature.auth.presentation.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

private val MAJORS = listOf("SE", "BM", "IT", "ME", "CIE", "AD", "AE")

@Composable
fun StudentRegistrationRoot(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    viewModel: RegistrationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RegistrationEvent.Registered -> onRegistered()
            is RegistrationEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    StudentRegistrationScreen(
        state = state,
        onBack = onBack,
        onRegister = viewModel::registerStudent,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(
    state: RegistrationState,
    onBack: () -> Unit,
    onRegister: (fullName: String, username: String, studentId: String, major: String, password: String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var studentId by rememberSaveable { mutableStateOf("") }
    var major by rememberSaveable { mutableStateOf(MAJORS.first()) }
    var password by rememberSaveable { mutableStateOf("") }
    var majorExpanded by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val usernameHasSpace = username.contains(" ")
    val canSubmit = fullName.isNotBlank() && username.isNotBlank() && studentId.isNotBlank() &&
        password.isNotBlank() && !usernameHasSpace && !state.isLoading

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Registration") },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "Back") } },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullName,
                onValueChange = { fullName = it },
                singleLine = true,
                label = { Text(text = "Full name") },
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = { if (it.length <= 30) username = it },
                singleLine = true,
                isError = usernameHasSpace,
                label = { Text(text = "Username") },
                supportingText = { if (usernameHasSpace) Text(text = "No spaces allowed") },
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = studentId,
                onValueChange = { if (it.length <= 7) studentId = it },
                singleLine = true,
                label = { Text(text = "Student ID") },
            )

            MajorPicker(
                major = major,
                expanded = majorExpanded,
                onExpandedChange = { majorExpanded = it },
                onMajorSelected = { major = it },
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                label = { Text(text = "Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(text = if (passwordVisible) "Hide" else "Show")
                    }
                },
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canSubmit,
                onClick = { onRegister(fullName, username, studentId, major, password) },
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = "Sign up")
                }
            }
        }
    }
}

@Composable
private fun MajorPicker(
    major: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onMajorSelected: (String) -> Unit,
) {
    Box {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = major,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Major") },
            trailingIcon = { Text(text = if (expanded) "▲" else "▼") },
        )
        // Transparent overlay: OutlinedTextField swallows clicks, so anchor the menu here.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onExpandedChange(true) },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            MAJORS.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onMajorSelected(item)
                        onExpandedChange(false)
                    },
                )
            }
        }
    }
}
