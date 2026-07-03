package com.example.twitturin.home.presentation.settings.bottom_bar.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twitturin.R
import com.example.twitturin.home.presentation.settings.bottom_bar.onBoarding.screens.BnvPreviewScreen
import kotlinx.coroutines.launch

private data class BnvPage(val imageRes: Int, val description: String)

@Composable
fun CustomBnvScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onCustomize: () -> Unit
) {
    val pages = listOf(
        BnvPage(R.drawable.labeled, "This view represents a bottom navigation view in LABELED state."),
        BnvPage(R.drawable.selected, "This view represents a bottom navigation view in SELECTED state."),
        BnvPage(R.drawable.unlabeled, "This view represents a bottom navigation view in UN LABELED state.")
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Text(
                text = "Change your navigation bar UI",
                style = MaterialTheme.typography.titleMedium
            )
        }

        TabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, _ ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = "${index + 1}") }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) { page ->
            BnvPreviewScreen(
                imageRes = pages[page].imageRes,
                description = pages[page].description
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCustomize
        ) {
            Text(text = "Customize")
        }
    }
}
