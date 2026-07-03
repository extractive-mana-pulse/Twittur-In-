package com.example.twitturin.feature.auth.presentation.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.BrandTextField
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.PasswordField
import com.example.twitturin.core.designsystem.component.PrimaryButton
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfessorRegistrationRoot(
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

    ProfessorRegistrationScreen(
        state = state,
        onBack = onBack,
        onRegister = viewModel::registerProfessor,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ProfessorRegistrationScreen(
    state: RegistrationState,
    onBack: () -> Unit,
    onRegister: (fullName: String, username: String, subject: String, password: String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val usernameHasSpace = username.contains(" ")
    val canSubmit = fullName.isNotBlank() && username.isNotBlank() && subject.isNotBlank() &&
        password.isNotBlank() && !usernameHasSpace && !state.isLoading

    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = "Create account", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Professor account",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp),
            )

            BrandTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Full name",
                leadingIcon = TwitturIcons.Account,
            )
            BrandTextField(
                value = username,
                onValueChange = { if (it.length <= 30) username = it },
                placeholder = "Username",
                leadingIcon = TwitturIcons.PersonAdd,
            )
            if (usernameHasSpace) {
                Text("No spaces allowed", color = Danger, style = MaterialTheme.typography.bodySmall)
            }
            BrandTextField(
                value = subject,
                onValueChange = { subject = it },
                placeholder = "Subject",
                leadingIcon = TwitturIcons.Edit,
            )
            PasswordField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                visible = passwordVisible,
                onToggleVisible = { passwordVisible = !passwordVisible },
                imeAction = ImeAction.Done,
            )

            PrimaryButton(
                text = "Sign up",
                onClick = { onRegister(fullName, username, subject, password) },
                enabled = canSubmit,
                loading = state.isLoading,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
