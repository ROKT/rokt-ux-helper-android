package com.rokt.demoapp.ui.screen.tutorials

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.screen.tutorials.eight.TutorialEightActivity
import com.rokt.demoapp.ui.screen.tutorials.five.TutorialFiveActivity
import com.rokt.demoapp.ui.screen.tutorials.four.TutorialFourCompose
import com.rokt.demoapp.ui.screen.tutorials.one.TutorialOneCompose
import com.rokt.demoapp.ui.screen.tutorials.seven.TutorialSevenActivity
import com.rokt.demoapp.ui.screen.tutorials.six.TutorialSixActivity
import com.rokt.demoapp.ui.screen.tutorials.three.TutorialThreeCompose
import com.rokt.demoapp.ui.screen.tutorials.two.TutorialTwoCompose

@Immutable
data class TutorialScreenState(
    val title: String = "Choose a tutorial",
    val description: String = "Tutorials are a great way to learn about Rokt's features and how to use them.",
    val items: List<TutorialPageListItem> = listOf(),
)

@Immutable
sealed class TutorialPageListItem(
    val title: String,
    val description: String,
    val drawableResource: Int,
) {
    class ComposablePageListItem(
        title: String,
        description: String,
        drawableResource: Int,
        val navAction: DestinationType,
        val content: @Composable () -> Unit,
    ) : TutorialPageListItem(title, description, drawableResource)

    class AndroidXmlPageListItem(
        title: String,
        description: String,
        drawableResource: Int,
        val startActivity: (Context) -> Unit,
    ) : TutorialPageListItem(title, description, drawableResource)
}

@Composable
internal fun rememberTutorialPageState(backPressed: () -> Unit): TutorialScreenState = remember {
    TutorialScreenState(
        items = listOf(
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 1",
                description = "This tutorial is for application on Compose, using Default System fonts",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_ONE_COMPOSE,
                content = { TutorialOneCompose(backPressed) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 2",
                description = "This tutorial is for application on Compose on passing custom fonts",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_TWO_COMPOSE,
                content = { TutorialTwoCompose(backPressed) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 3",
                description = "This tutorial is for application on Compose on sending platform Events",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_THREE_COMPOSE,
                content = { TutorialThreeCompose(backPressed) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 4",
                description = "This tutorial is for application on Compose on handling UrlOpen UX event",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_FOUR_COMPOSE,
                content = { TutorialFourCompose(backPressed) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 5",
                description = "This tutorial is for application on Android XML, using Default System fonts",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialFiveActivity.startActivity(context) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 6",
                description = "This tutorial is for application on Android XML, on passing custom fonts",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialSixActivity.startActivity(context) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 7",
                description = "This tutorial is for application on Android XML, on sending platform Events",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialSevenActivity.startActivity(context) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 8",
                description = "This tutorial is for application on Android XML, on handling UrlOpen UX event",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialEightActivity.startActivity(context) },
            ),
        ),
    )
}
