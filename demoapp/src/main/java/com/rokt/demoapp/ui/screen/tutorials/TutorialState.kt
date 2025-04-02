package com.rokt.demoapp.ui.screen.tutorials

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.screen.tutorials.eight.TutorialEightActivity
import com.rokt.demoapp.ui.screen.tutorials.five.TutorialFiveActivity
import com.rokt.demoapp.ui.screen.tutorials.four.TutorialFourCompose
import com.rokt.demoapp.ui.screen.tutorials.nine.TutorialNineCompose
import com.rokt.demoapp.ui.screen.tutorials.one.TutorialOneCompose
import com.rokt.demoapp.ui.screen.tutorials.playground.TutorialPlaygroundCompose
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
sealed class TutorialPageListItem(val title: String, val description: String, val drawableResource: Int) {
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
                title = "Playground Activity",
                description = "Application using Compose and Default System fonts",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_PLAYGROUND_COMPOSE,
                content = { TutorialPlaygroundCompose(backPressed) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 1",
                description = "Application using Compose and Default System fonts",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_ONE_COMPOSE,
                content = { TutorialOneCompose(backPressed) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 2",
                description = "Application using Compose and custom fonts",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_TWO_COMPOSE,
                content = { TutorialTwoCompose(backPressed) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 3",
                description = "Application using Compose and sends platform Events",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_THREE_COMPOSE,
                content = { TutorialThreeCompose(backPressed) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 4",
                description = "Application using Compose and handle UrlOpen UX event",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_FOUR_COMPOSE,
                content = { TutorialFourCompose(backPressed) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 5",
                description = "Application using XML and Default System fonts",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialFiveActivity.startActivity(context) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 6",
                description = "Application using XML and custom fonts",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialSixActivity.startActivity(context) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 7",
                description = "Application using XML and sends platform Events",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialSevenActivity.startActivity(context) },
            ),
            TutorialPageListItem.AndroidXmlPageListItem(
                title = "Tutorial 8",
                description = "Application using XML and handle UrlOpen UX event",
                drawableResource = R.drawable.ic_primary_button,
                startActivity = { context -> TutorialEightActivity.startActivity(context) },
            ),
            TutorialPageListItem.ComposablePageListItem(
                title = "Tutorial 9",
                description = "Application using Compose and handle Thankyou page Ux events",
                drawableResource = R.drawable.ic_primary_button,
                navAction = DestinationType.TUTORIAL_NINE_COMPOSE,
                content = { TutorialNineCompose(backPressed) },
            ),
        ),
    )
}
