package com.rokt.demoapp.ui.screen

import androidx.navigation.NavController

/**
 * Destinations used in RoktDemo app.
 */
object MainDestinations {
    const val HOME = "home"
    const val TUTORIAL = "tutorial"
    const val LAYOUTS = "layouts"
    const val CUSTOM = "custom"
    const val SETTINGS = "settings"
}

/**
 * Models the navigation actions in the app.
 */

class MainActions constructor(navController: NavController) {
    val tutorialLibraryClicked: () -> Unit = {
        navController.navigate(MainDestinations.TUTORIAL)
    }

    val demoLayoutsClicked: () -> Unit = {
        navController.navigate(MainDestinations.LAYOUTS)
    }

    val settingsClicked: () -> Unit = {
        navController.navigate(MainDestinations.SETTINGS)
    }

    val customBuilderClicked: () -> Unit = {
        navController.navigate(MainDestinations.CUSTOM)
    }

    val backPressed: () -> Unit = {
        navController.navigateUp()
    }
}
