package com.wioletamwrobel.wieluncityapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wioletamwrobel.wieluncityapp.ui.MyBeautifulCityViewModel
import com.wioletamwrobel.wieluncityapp.ui.MyBeautifulCityViewModel.MyBeautifulCityUiState
import com.wioletamwrobel.wieluncityapp.ui.WielunCityApp
import com.wioletamwrobel.wieluncityapp.ui.theme.WielunCityAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val prefs by lazy {
        applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    private val viewModel = MyBeautifulCityViewModel()
    private lateinit var uiState: State<MyBeautifulCityUiState>

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WielunCityAppTheme {
                uiState = viewModel.uiState.collectAsState()
                Surface {
                    val windowSize = calculateWindowSizeClass(activity = this)
                    val context = LocalContext.current
//                    viewModel.findBeacon(this, this)
                    Navigation(
                        windowSize = windowSize,
                        onBackPressed = { finish() },
                        prefs = prefs,
                        context = context,
                        activity = this
                    )
                }
            }
        }
    }

    @Composable
    fun SplashScreen(
        navController: NavController
    ) {
        val scale = remember { Animatable(0f) }

        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = {
                        OvershootInterpolator(1f).getInterpolation(it)
                    }
                )
            )
            delay(3500L)
            navController.navigate("main_screen")
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.medium10),
                    vertical = dimensionResource(id = R.dimen.large25)
                )
        ) {
            Text(
                text = stringResource(id = R.string.city_name),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.large),
                    vertical = dimensionResource(id = R.dimen.medium10)
                )
            )
            Image(
                painter = painterResource(R.drawable.city_hall_vintage),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .scale(scale.value)
                    .width(300.dp)
                    .padding(
                        bottom = dimensionResource(id = R.dimen.medium),
                        top = dimensionResource(id = R.dimen.medium)
                    )
                    .clip(CircleShape)
                    .border(
                        dimensionResource(id = R.dimen.extra_small),
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
            )
            Text(
                text = stringResource(id = R.string.splash_screen_p1),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.large),
                        vertical = dimensionResource(id = R.dimen.medium10)
                    )
                    .scale(scale.value)
            )
            Text(
                text = stringResource(id = R.string.splash_screen_p2),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.large))
                    .scale(scale.value)
            )
        }
    }

    @Composable
    fun Navigation(
        windowSize: WindowSizeClass,
        onBackPressed: () -> Unit,
        prefs: SharedPreferences,
        context: Context,
        activity: Activity,
    ) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "splash_screen") {
            composable(route = "splash_screen") {
                SplashScreen(navController = navController)
            }
            composable(route = "main_screen") {
                WielunCityApp(
                    onBackPressed = onBackPressed,
                    windowSize = windowSize.widthSizeClass,
                    prefs = prefs,
                    context = context,
                    viewModel = viewModel,
                    uiState = uiState,
                    activity = activity,
                    navController = navController
                )
            }
        }
    }
}