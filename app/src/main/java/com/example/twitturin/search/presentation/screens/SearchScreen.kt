package com.example.twitturin.search.presentation.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.twitturin.R
import com.example.twitturin.notification.presentation.screens.AnimatedPreloader
import com.example.twitturin.search.domain.model.SearchResponse
import com.example.twitturin.search.presentation.sealed.SearchResource
import com.example.twitturin.search.presentation.vm.SearchViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var searchHistory = remember { mutableStateListOf<String>() }
    val searchResource by viewModel.searchUser.collectAsStateWithLifecycle()

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val spokenText: String? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (spokenText != null) query = spokenText
        }
    }

    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (active) 0.dp else 16.dp),
                query = query,
                onQueryChange = { query = it },
                onSearch = { newQuery ->
                    searchHistory.add(newQuery)
                    active = false
                    query = ""
                    viewModel.searchUser(newQuery)
                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text(text = "Search...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    Row {
                        if (query.isEmpty()) {
                            IconButton(onClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search...")
                                }
                                speechRecognizerLauncher.launch(intent)
                            }) {
                                Icon(Icons.Default.Mic, contentDescription = null)
                            }
                        } else {
                            IconButton(onClick = { query = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                            }
                        }
                    }
                }
            ) {
                if (searchHistory.isEmpty()) {
                    Text(
                        text = "Search history is empty",
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    searchHistory.take(10).forEach { item ->
                        ListItem(
                            modifier = Modifier.clickable { query = item },
                            headlineContent = { Text(text = item) },
                            leadingContent = {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = null
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (searchResource) {
                is SearchResource.Loading -> { CircularProgressIndicator() }
                is SearchResource.Success -> {
                    val users = (searchResource as SearchResource.Success<SearchResponse>).data.users
                    if (users.isEmpty()) {
                        Text(
                            text = "No results found",
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        users.forEach { user ->
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clickable {
                                    query = user.username.toString()
                                },
                                headlineContent = {
                                    Column {
                                        Text(
                                            text = user.fullName.toString(),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                                letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                                                platformStyle = MaterialTheme.typography.bodyLarge.platformStyle,
                                                textGeometricTransform = MaterialTheme.typography.bodyLarge.textGeometricTransform,
                                            )
                                        )
                                        Text(
                                            text = "@${user.username}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                                letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                                                platformStyle = MaterialTheme.typography.bodyMedium.platformStyle,
                                                textGeometricTransform = MaterialTheme.typography.bodyMedium.textGeometricTransform,
                                            )
                                        )
                                    }
                                },
                                leadingContent = {
                                    AsyncImage(
                                        model = user.profilePicture ?: Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp).clip(CircleShape)
                                    )
                                },
                            )
                            HorizontalDivider()
                        }
                    }
                }
                is SearchResource.Error -> {
                    Text(
                        text = (searchResource as SearchResource.Error<SearchResponse>).message,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
                }
                is SearchResource.Initial -> { InitialState() }
            }
        }
    }
}

@Composable
fun InitialState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedPreloader(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(282.dp),
            rawResId = R.raw.search_cat,
        )
    }
}