package com.samuelav.presentation.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/** Gives [content] a ViewModelStore of its own, as a navigation entry does in the app. */
@Composable
fun WithTestViewModelStore(content: @Composable () -> Unit) {
    val owner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner, content = content)
}
