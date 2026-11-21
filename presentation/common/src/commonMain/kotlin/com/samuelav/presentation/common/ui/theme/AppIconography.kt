package com.samuelav.presentation.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.samuelav.presentation.common.resources.Res
import com.samuelav.presentation.common.resources.ic_alert_outline
import com.samuelav.presentation.common.resources.ic_arrow_back
import com.samuelav.presentation.common.resources.ic_arrow_forward
import com.samuelav.presentation.common.resources.ic_bell_outline
import com.samuelav.presentation.common.resources.ic_bug_outline
import com.samuelav.presentation.common.resources.ic_chevron_down
import com.samuelav.presentation.common.resources.ic_chevron_left
import com.samuelav.presentation.common.resources.ic_chevron_right
import com.samuelav.presentation.common.resources.ic_chevron_up
import com.samuelav.presentation.common.resources.ic_close
import com.samuelav.presentation.common.resources.ic_code
import com.samuelav.presentation.common.resources.ic_data
import com.samuelav.presentation.common.resources.ic_delete_outline
import com.samuelav.presentation.common.resources.ic_dots_horizontal
import com.samuelav.presentation.common.resources.ic_email_outline
import com.samuelav.presentation.common.resources.ic_home
import com.samuelav.presentation.common.resources.ic_information_outline
import com.samuelav.presentation.common.resources.ic_information_variant
import com.samuelav.presentation.common.resources.ic_menu
import com.samuelav.presentation.common.resources.ic_pencil_outline
import com.samuelav.presentation.common.resources.ic_refresh
import com.samuelav.presentation.common.resources.ic_search
import com.samuelav.presentation.common.resources.ic_settings
import com.samuelav.presentation.common.resources.ic_share_variant_outline
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 *  [Icons reference](https://fonts.google.com/icons?icon.style=Rounded)
 */
object AppIconography {
    val home: Icon = Icon(resource = Res.drawable.ic_home)
    val search: Icon = Icon(resource = Res.drawable.ic_search)
    val settings: Icon = Icon(resource = Res.drawable.ic_settings)
    val notifications: Icon = Icon(resource = Res.drawable.ic_bell_outline)
    val more: Icon = Icon(resource = Res.drawable.ic_dots_horizontal)
    val menu: Icon = Icon(resource = Res.drawable.ic_menu)
    val close: Icon = Icon(resource = Res.drawable.ic_close)
    val chevronLeft: Icon = Icon(resource = Res.drawable.ic_chevron_left)
    val chevronRight: Icon = Icon(resource = Res.drawable.ic_chevron_right)
    val chevronUp: Icon = Icon(resource = Res.drawable.ic_chevron_up)
    val chevronDown: Icon = Icon(resource = Res.drawable.ic_chevron_down)
    val arrowBack: Icon = Icon(resource = Res.drawable.ic_arrow_back)
    val arrowForward: Icon = Icon(resource = Res.drawable.ic_arrow_forward)
    val refresh: Icon = Icon(resource = Res.drawable.ic_refresh)
    val info: Icon = Icon(resource = Res.drawable.ic_information_outline)
    val infoVariant: Icon = Icon(resource = Res.drawable.ic_information_variant)
    val modify: Icon = Icon(resource = Res.drawable.ic_pencil_outline)
    val delete: Icon = Icon(resource = Res.drawable.ic_delete_outline)
    val share: Icon = Icon(resource = Res.drawable.ic_share_variant_outline)
    val email: Icon = Icon(resource = Res.drawable.ic_email_outline)
    val bug: Icon = Icon(resource = Res.drawable.ic_bug_outline)
    val data: Icon = Icon(resource = Res.drawable.ic_data)
    val code: Icon = Icon(resource = Res.drawable.ic_code)
    val alert: Icon = Icon(resource = Res.drawable.ic_alert_outline)
}

data class Icon(val resource: DrawableResource) {
    val painter: Painter
        @Composable get() = painterResource(resource)
}
