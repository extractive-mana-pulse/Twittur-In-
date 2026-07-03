package com.example.twitturin.core.designsystem.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.BrandDeep
import com.example.twitturin.core.designsystem.theme.dmSansFamily

/** The "Twittur(In)" wordmark — DM Sans italic 800, Brand with the "(In)" in Brand Deep. */
@Composable
fun TwitturLogo(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 40.sp,
) {
    Text(
        modifier = modifier,
        style = LocalTextStyle.current.copy(
            fontFamily = dmSansFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            fontSize = fontSize,
            letterSpacing = (-1).sp,
        ),
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Brand)) { append("Twittur") }
            withStyle(SpanStyle(color = BrandDeep)) { append("(In)") }
        },
    )
}
