package com.samuelav.presentation.common.app.navigation

import androidx.navigation3.runtime.NavKey
import com.samuelav.presentation.common.resources.Res
import com.samuelav.presentation.common.resources.nav_item_home
import com.samuelav.presentation.common.resources.nav_item_more
import com.samuelav.presentation.common.resources.nav_item_search
import com.samuelav.presentation.common.ui.theme.AppIconography
import com.samuelav.presentation.common.ui.theme.Icon
import org.jetbrains.compose.resources.StringResource

sealed class NavItem(
    val navKey: NavKey,
    val icon: Icon,
    val title: StringResource,
    val contentDescription: StringResource,
) {
    data object Home : NavItem(
        navKey = AppNavKey.Home.Main,
        icon = AppIconography.home,
        title = Res.string.nav_item_home,
        contentDescription = Res.string.nav_item_home,
    )
    data object Search : NavItem(
        navKey = AppNavKey.Search.Main,
        icon = AppIconography.search,
        title = Res.string.nav_item_search,
        contentDescription = Res.string.nav_item_search,
    )
    data object More : NavItem(
        navKey = AppNavKey.More.Main,
        icon = AppIconography.more,
        title = Res.string.nav_item_more,
        contentDescription = Res.string.nav_item_more,
    )
}
