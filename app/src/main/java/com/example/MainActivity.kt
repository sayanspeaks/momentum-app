package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MomentumTheme
import com.example.viewmodel.MomentumViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MomentumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomentumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val isOnboarded by viewModel.isOnboarded.collectAsState()
                    val isAiThinking by viewModel.isAiThinking.collectAsState()

                    Crossfade(
                        targetState = isOnboarded,
                        animationSpec = tween(durationMillis = 500),
                        label = "ScreenTransition"
                    ) { onboarded ->
                        if (onboarded) {
                            DashboardScreen(
                                viewModel = viewModel,
                                onReset = { viewModel.resetData() }
                            )
                        } else {
                            OnboardingScreen(
                                onBuildDay = { text -> viewModel.createWithNaturalLanguage(text) },
                                onTryDemo = { viewModel.loadDemoData() },
                                isThinking = isAiThinking
                            )
                        }
                    }
                }
            }
        }
    }
}
