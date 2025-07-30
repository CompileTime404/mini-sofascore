package com.example.minisofascore

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.minisofascore.navigation.NavigationRoot
import com.example.minisofascore.ui.settingspage.SettingsPageViewModel
import com.example.minisofascore.ui.theme.MiniSofascoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val view = LocalView.current
            val window = (view.context as Activity).window
            SideEffect {
                WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = false
            }

            val settingsPageViewModel: SettingsPageViewModel = hiltViewModel()
            val isDarkTheme by settingsPageViewModel.isDarkTheme.collectAsState(initial = false)

            MiniSofascoreTheme(darkTheme = isDarkTheme) {
                NavigationRoot()
            }
        }
    }
}