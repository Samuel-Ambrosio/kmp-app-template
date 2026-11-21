package com.samuelav.presentation.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
internal actual fun rememberTextSharer(): TextSharer =
    remember {
        TextSharer { _, text ->
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
        }
    }
