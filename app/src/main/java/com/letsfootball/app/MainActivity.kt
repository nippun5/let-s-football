package com.letsfootball.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.letsfootball.app.ui.screens.fixtures.FixturesScreen
import com.letsfootball.app.ui.screens.fixtures.FixturesViewModel
import com.letsfootball.app.ui.theme.LetsFootballTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FixturesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LetsFootballTheme {
                FixturesScreen(viewModel = viewModel)
            }
        }
    }
}
