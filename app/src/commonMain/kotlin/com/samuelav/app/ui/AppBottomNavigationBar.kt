package com.samuelav.app.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samuelav.presentation.common.app.AppState
import com.samuelav.presentation.common.app.navigation.NavItem
import com.samuelav.presentation.common.extensions.clearAndAdd
import com.samuelav.presentation.common.ui.theme.AppTheme.colors
import org.jetbrains.compose.resources.stringResource

private const val UNSELECTED_ALPHA = 0.6f

@Composable
internal fun AppBottomNavigationBar(appState: AppState) {
    val navItems = appState.appConfiguration.navItems
    val lastNavItemKey =
        appState.navBackStack.lastOrNull { navKey ->
            navKey::class in navItems.map { it.navKey::class }
        }
    val currentNavItem = navItems.firstOrNull { navItem ->
        lastNavItemKey != null && navItem.navKey::class == lastNavItemKey::class
    } ?: NavItem.Home

    NavigationBar(containerColor = colors.surface, contentColor = colors.primary) {
        navItems.forEach { navItem ->
            NavigationBarItem(
                icon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = navItem.icon.painter,
                        contentDescription = stringResource(navItem.contentDescription),
                    )
                },
                selected = navItem == currentNavItem,
                onClick = {
                    appState.navBackStack.clearAndAdd(NavItem.Home.navKey, navItem.navKey)
                },
                // Keeps the Material 2 look: no selection pill, unselected items dimmed
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    unselectedIconColor = colors.primary.copy(alpha = UNSELECTED_ALPHA),
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}
