package com.example.twitturin.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.twitturin.core.designsystem.component.BrandTextField
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.GradientAvatar
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.OnBrand
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
            BrandTopBar(
                title = "Edit profile",
                onBack = onBack,
                actions = {
                    Button(
                        onClick = { onSave(fullName, username, email, bio, country, birthday) },
                        enabled = !state.isLoading,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Brand, contentColor = OnBrand),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = OnBrand, strokeWidth = 2.dp)
                        } else {
                            Text("Save", fontWeight = FontWeight.Bold)
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Avatar with an add-photo badge — the picker itself is a deferred expect/actual task.
            Box(modifier = Modifier.padding(top = 8.dp)) {
                Box(modifier = Modifier.size(96.dp)) {
                    GradientAvatar(name = fullName.ifBlank { "?" }, size = 96.dp)
                    if (!user?.profilePicture.isNullOrBlank()) {
                        AsyncImage(
                            model = user?.profilePicture,
                            contentDescription = null,
                            modifier = Modifier.size(96.dp).clip(CircleShape),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brand),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(TwitturIcons.AddPhoto, contentDescription = "Change photo", tint = OnBrand, modifier = Modifier.size(18.dp))
                }
            }

            BrandTextField(fullName, { fullName = it }, placeholder = "Full name", leadingIcon = TwitturIcons.Account)
            BrandTextField(username, { username = it }, placeholder = "Username", leadingIcon = TwitturIcons.PersonAdd)
            BrandTextField(email, { email = it }, placeholder = "Email", leadingIcon = TwitturIcons.Mail)
            BrandTextField(country, { country = it }, placeholder = "Country", leadingIcon = TwitturIcons.Info)
            BrandTextField(birthday, { birthday = it }, placeholder = "Birthday", leadingIcon = TwitturIcons.Edit)
            BrandTextField(bio, { bio = it }, placeholder = "Biography", leadingIcon = null, singleLine = false)
        }
    }
}
