package com.wioletamwrobel.wieluncityapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat
import com.wioletamwrobel.wieluncityapp.R

private val lightColorsTheme = lightColorScheme(
    primary = light_primary, //yellow
    secondary = light_secondary,//violet
    tertiary = light_tertiary, //green


    primaryContainer = light_primaryContainer, //white
    onPrimaryContainer = light_onPrimaryContainer,

    outline = light_outline,
    outlineVariant = light_outlineVariant,
    surface = light_surface, //green
)


private val darkColorsTheme = darkColorScheme(
    primary = dark_primary,
    secondary = dark_secondary,
    tertiary = dark_tertiary,

    primaryContainer = dark_primaryContainer,
    onPrimaryContainer = dark_onPrimaryContainer,

    outline = dark_outline,
    outlineVariant = dark_outlineVariant,

    surface = dark_surface,
)

val Calistogar = FontFamily(Font(R.font.calistogar_regular))
val SenRegular = FontFamily(Font(R.font.sen_regular))
val SenSemiBold = FontFamily(Font(R.font.sen_semibold))

@Composable
fun WielunCityAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorsTheme
        else -> lightColorsTheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = Shapes
    )
}