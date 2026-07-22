package com.example.twitturin.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.twitturin.core.designsystem.resources.Res
import com.example.twitturin.core.designsystem.resources.dmsans_bold
import com.example.twitturin.core.designsystem.resources.dmsans_extrabolditalic
import com.example.twitturin.core.designsystem.resources.dmsans_medium
import com.example.twitturin.core.designsystem.resources.dmsans_regular
import com.example.twitturin.core.designsystem.resources.dmsans_semibold
import org.jetbrains.compose.resources.Font

@Composable
fun dmSansFamily(): FontFamily = FontFamily(
    Font(Res.font.dmsans_regular, FontWeight.Normal),
    Font(Res.font.dmsans_medium, FontWeight.Medium),
    Font(Res.font.dmsans_semibold, FontWeight.SemiBold),
    Font(Res.font.dmsans_bold, FontWeight.Bold),
    Font(Res.font.dmsans_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
)

@Composable
fun twitturTypography(): Typography {
    val dm = dmSansFamily()
    val d = Typography()
    fun TextStyle.dm() = copy(fontFamily = dm)
    return Typography(
        displayLarge = d.displayLarge.dm(),
        displayMedium = d.displayMedium.dm(),
        displaySmall = d.displaySmall.dm(),
        headlineLarge = d.headlineLarge.copy(fontFamily = dm, fontWeight = FontWeight.Bold),
        headlineMedium = d.headlineMedium.copy(fontFamily = dm, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp),
        headlineSmall = d.headlineSmall.copy(fontFamily = dm, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
        titleLarge = d.titleLarge.copy(fontFamily = dm, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
        titleMedium = d.titleMedium.copy(fontFamily = dm, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
        titleSmall = d.titleSmall.copy(fontFamily = dm, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
        bodyLarge = d.bodyLarge.copy(fontFamily = dm, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
        bodyMedium = d.bodyMedium.copy(fontFamily = dm, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = d.bodySmall.copy(fontFamily = dm, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
        labelLarge = d.labelLarge.copy(fontFamily = dm, fontWeight = FontWeight.Bold, fontSize = 15.sp),
        labelMedium = d.labelMedium.copy(fontFamily = dm, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
        labelSmall = d.labelSmall.copy(fontFamily = dm, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.2.sp),
    )
}