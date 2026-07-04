package com.example.twitturin.chat.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.twitturin.R

@Preview(showBackground = true, showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen() {
    var state by remember { mutableIntStateOf(0) }

    val icons = listOf(
        R.drawable.chat,
        R.drawable.groups,
    )
    Scaffold(
        /*** Added windowInsetsPadding to the Scaffold with WindowInsets.safeDrawing.only(WindowInsetsSides.Top) to handle the safe drawing area */
//        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        topBar = {
            PrimaryTabRow(
                selectedTabIndex = state,
                modifier = Modifier.statusBarsPadding()
            ) {
                icons.forEachIndexed { index, iconRes ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        icon = {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null
                            )
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Primary tab ${state + 1} selected",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}