package com.samuelav.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.samuelav.presentation.common.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        AppScaffold(
            modifier =
            Modifier.fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        )
    }
}
