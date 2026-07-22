package com.example.twitturin.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.BrandTextField
import com.example.twitturin.core.designsystem.component.PasswordField
import com.example.twitturin.core.designsystem.component.PrimaryButton
import com.example.twitturin.core.designsystem.component.TwitturLogo
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInRoot(
    onSignedIn: () -> Unit,
    onSignUp: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LoginEvent.LoggedIn -> onSignedIn()
            is LoginEvent.ShowError -> pendingError = event.message
        }
    }

    pendingError?.let { error ->
        val message = error.asString()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            pendingError = null
        }
    }

    SignInScreen(
        state = state,
        onLogin = viewModel::login,
        onSignUp = onSignUp,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun SignInScreen(
    state: LoginState,
    onLogin: (username: String, password: String) -> Unit,
    onSignUp: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val canSubmit = username.isNotBlank() && password.isNotBlank() && !state.isLoading
    fun submit() { if (canSubmit) onLogin(username.trim(), password) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(40.dp))
            TwitturLogo(fontSize = 44.sp)
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
                modifier = Modifier.padding(top = 6.dp, bottom = 32.dp),
            )

            BrandTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = LocalStrings.current.username,
                leadingIcon = TwitturIcons.Account,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Next),
            )
            Spacer(Modifier.height(14.dp))
            PasswordField(
                value = password,
                onValueChange = { password = it },
                placeholder = LocalStrings.current.password,
                visible = passwordVisible,
                onToggleVisible = { passwordVisible = !passwordVisible },
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = LocalStrings.current.signIn,
                onClick = { submit() },
                enabled = canSubmit,
                loading = state.isLoading,
            )

            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Don't have an account?", style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
                TextButton(onClick = onSignUp) {
                    Text(LocalStrings.current.signUp, color = Brand, style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
