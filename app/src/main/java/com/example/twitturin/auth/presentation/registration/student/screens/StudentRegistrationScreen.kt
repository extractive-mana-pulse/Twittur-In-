package com.example.twitturin.auth.presentation.registration.student.screens

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.registration.student.sealed.SignUpStudentResult
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel,
    onBack: () -> Unit,
    onRegistered: () -> Unit
) {
    val majors = stringArrayResource(R.array.major_array).toList()

    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var studentId by rememberSaveable { mutableStateOf("") }
    var major by rememberSaveable { mutableStateOf(majors.firstOrNull().orEmpty()) }
    var password by rememberSaveable { mutableStateOf("") }
    var majorExpanded by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val result by viewModel.signUpStudentResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val usernameHasSpace = username.contains(" ")
    val isLoading = submitted && result is SignUpStudentResult.Loading
    val canSubmit = username.isNotBlank() && studentId.isNotBlank() &&
            password.isNotBlank() && !usernameHasSpace && !isLoading

    LaunchedEffect(result, submitted) {
        if (!submitted) return@LaunchedEffect
        when (val current = result) {
            is SignUpStudentResult.Success -> onRegistered()
            is SignUpStudentResult.Error -> {
                snackbarHostState.showSnackbar(current.message)
                submitted = false
            }
            SignUpStudentResult.Loading -> Unit
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
                value = studentId,
                onValueChange = { if (it.length <= 7) studentId = it },
                singleLine = true,
                label = { Text(text = stringResource(R.string.student_id)) },
                leadingIcon = { Icon(painterResource(R.drawable.id_icon), contentDescription = null) }
            )

            ExposedDropdownMenuBox(
                expanded = majorExpanded,
                onExpandedChange = { majorExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = major,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.major)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = majorExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = majorExpanded,
                    onDismissRequest = { majorExpanded = false }
                ) {
                    majors.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                major = item
                                majorExpanded = false
                            }
                        )
                    }
                }
            }

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
                    viewModel.signUpStudent(
                        fullName.trim(),
                        username.trim(),
                        studentId.trim(),
                        major,
                        password.trim(),
                        "student"
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
