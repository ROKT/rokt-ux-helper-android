package com.rokt.demoapp.ui.screen.tutorials

import androidx.navigation.NavController
import com.rokt.demoapp.ui.screen.tutorials.TutorialDestinations.TUTORIAL_DESTINATION

/**
 * Destinations used the Tutorial page.
 */
object TutorialDestinations {
    const val TUTORIAL_HOME = "tutorial_home"
    const val TUTORIAL_DESTINATION = "tutorial_destination"
}

enum class DestinationType(val value: String) {
    TUTORIAL_PLAYGROUND_COMPOSE("tutorial_playground_compose"),
    TUTORIAL_ONE_COMPOSE("tutorial_one_compose"),
    TUTORIAL_TWO_COMPOSE("tutorial_two_compose"),
    TUTORIAL_THREE_COMPOSE("tutorial_three_compose"),
    TUTORIAL_FOUR_COMPOSE("tutorial_four_compose"),
    TUTORIAL_NINE_COMPOSE("tutorial_nine_compose"),
}

/**
 * Models the navigation actions in the Tutorial Page.
 */

class TutorialActions constructor(navController: NavController) {
    val navigateToDemoDestination: (destination: DestinationType) -> Unit = {
        navController.navigate("$TUTORIAL_DESTINATION${it.value}")
    }
    val backPressed: () -> Unit = {
        navController.navigateUp()
    }
}
