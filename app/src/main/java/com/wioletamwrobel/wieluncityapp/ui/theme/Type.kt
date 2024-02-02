package com.wioletamwrobel.wieluncityapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Calistogar,
        fontSize = 42.sp,
        letterSpacing = 0.8.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Calistogar,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SenSemiBold,
        fontSize = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SenRegular,
        fontSize = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SenSemiBold,
        fontSize = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SenRegular,
        fontSize = 15.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SenRegular,
        fontSize = 13.sp
    )
)