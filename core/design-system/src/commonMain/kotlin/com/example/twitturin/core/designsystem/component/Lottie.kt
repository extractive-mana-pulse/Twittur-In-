package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.resources.Res
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

/**
 * The Lottie animations carried over from the legacy app (`app/src/main/res/raw`), vendored as
 * compose-resources files so every platform can render them via Compottie.
 * [loop] mirrors the legacy `lottie_loop` flags: ambient scenes loop, one-shot reveals play once.
 */
enum class LottieAsset(internal val file: String, internal val loop: Boolean) {
    SearchCat("search_cat.json", true),
    GearSearch("gear_search.json", true),
    EmptyTweets("empty_tweets_list.json", false),
    EmptyLikes("empty_likes_list.json", false),
    EmptyNotifications("empty_notifications_list.json", false),
    Person("person_anim.json", false),
}

/** Renders one of the bundled [LottieAsset]s. Keeps its size while the JSON loads. */
@Composable
fun TwitturLottie(
    asset: LottieAsset,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(asset) {
        LottieCompositionSpec.JsonString(Res.readBytes("files/${asset.file}").decodeToString())
    }
    Image(
        painter = rememberLottiePainter(
            composition = composition,
            iterations = if (asset.loop) Compottie.IterateForever else 1,
        ),
        contentDescription = null,
        modifier = modifier,
    )
}

/** [EmptyState] variant with a Lottie animation instead of the icon circle. */
@Composable
fun LottieEmptyState(
    asset: LottieAsset,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    animationSize: Dp = 200.dp,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TwitturLottie(asset = asset, modifier = Modifier.size(animationSize))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Brand,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp),
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
