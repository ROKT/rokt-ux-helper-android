package com.rokt.demoapp.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rokt.demoapp.ui.screen.custom.CustomCheckoutPage
import com.rokt.demoapp.ui.screen.home.HomePage
import com.rokt.demoapp.ui.screen.layouts.DemoLayoutsPage
import com.rokt.demoapp.ui.screen.tutorials.TutorialPageContent
import com.rokt.demoapp.ui.theme.RoktAndroidSdkTheme

@Composable
fun RoktDemoApp(viewModel: MainActivityViewModel) {
    RoktAndroidSdkTheme {
        val navController = rememberNavController()
        val actions = remember(navController) { MainActions(navController) }
        NavHost(navController = navController, startDestination = MainDestinations.HOME) {
            composable(MainDestinations.HOME) {
                HomePage(actions)
            }

            composable(MainDestinations.TUTORIAL) {
                TutorialPageContent(actions.backPressed)
            }

            composable(MainDestinations.LAYOUTS) {
                DemoLayoutsPage(actions.backPressed)
            }

            composable(MainDestinations.CUSTOM) {
                CustomCheckoutPage(actions.backPressed)
            }
        }
        viewModel.previewParameterString.value?.let {
            actions.demoLayoutsClicked()
        }
    }
}
