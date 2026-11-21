package com.samuelav.presentation.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.samuelav.presentation.common.resources.Res
import com.samuelav.presentation.common.resources.roboto_black
import com.samuelav.presentation.common.resources.roboto_bold
import com.samuelav.presentation.common.resources.roboto_regular
import org.jetbrains.compose.resources.Font
import androidx.compose.material3.Typography as MaterialTypography

@ConsistentCopyVisibility
data class AppTypography internal constructor(
    val headingH1: TextStyle,
    val headingH2: TextStyle,
    val bodyLargeBold: TextStyle,
    val bodyLargeRegular: TextStyle,
    val bodyMediumBold: TextStyle,
    val bodyMediumRegular: TextStyle,
    val bodySmallBold: TextStyle,
    val bodySmallRegular: TextStyle,
    val button: TextStyle,
    val materialTypography: MaterialTypography
) {
    constructor(
        fontFamily: FontFamily = FontFamily.Default,
        headingH1: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        ),
        headingH2: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        ),
        bodyLargeBold: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        ),
        bodyLargeRegular: TextStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
        ),
        bodyMediumBold: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        ),
        bodyMediumRegular: TextStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        bodySmallBold: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        ),
        bodySmallRegular: TextStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        ),
        button: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        ),
    ) : this(
        headingH1 = headingH1.copy(fontFamily = fontFamily),
        headingH2 = headingH2.copy(fontFamily = fontFamily),
        bodyLargeBold = bodyLargeBold.copy(fontFamily = fontFamily),
        bodyLargeRegular = bodyLargeRegular.copy(fontFamily = fontFamily),
        bodyMediumBold = bodyMediumBold.copy(fontFamily = fontFamily),
        bodyMediumRegular = bodyMediumRegular.copy(fontFamily = fontFamily),
        bodySmallBold = bodySmallBold.copy(fontFamily = fontFamily),
        bodySmallRegular = bodySmallRegular.copy(fontFamily = fontFamily),
        button = button.copy(fontFamily = fontFamily),
        materialTypography =
        MaterialTypography(
            headlineLarge = headingH1.copy(fontFamily = fontFamily),
            headlineMedium = headingH2.copy(fontFamily = fontFamily),
            titleLarge = bodyLargeBold.copy(fontFamily = fontFamily),
            titleMedium = bodyMediumBold.copy(fontFamily = fontFamily),
            bodyLarge = bodyLargeRegular.copy(fontFamily = fontFamily),
            bodyMedium = bodyMediumRegular.copy(fontFamily = fontFamily),
            bodySmall = bodySmallRegular.copy(fontFamily = fontFamily),
            labelLarge = button.copy(fontFamily = fontFamily),
        ),
    )
}

// Compose Multiplatform loads font resources asynchronously, so the family can only be built in composition
@Composable
internal fun appFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.roboto_bold, FontWeight.Bold),
        Font(Res.font.roboto_black, FontWeight.Black),
        Font(Res.font.roboto_regular, FontWeight.Normal),
    )
