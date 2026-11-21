package com.samuelav.presentation.features.advert

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.samuelav.presentation.analytics.base.ScreenAnalytics
import org.koin.compose.koinInject

// TODO: Set the AdMob banner ad unit id
private const val AD_UNIT_ID_BANNER = ""

@Composable
@Suppress("UnusedParameter", "UnusedPrivateProperty")
actual fun AdvertBanner(modifier: Modifier) {
    val screenAnalytics: ScreenAnalytics = koinInject()

    // TODO: Remove '/**/' after google-services.json file and ad id are added
    /*AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AD_UNIT_ID_BANNER
                loadAd(AdRequest.Builder().build())
                adListener = object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        screenAnalytics.notifyAction(AdvertScreenAnalytics.Click)
                    }
                }
            }
        }
    )*/
}
