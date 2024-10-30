package com.rokt.demoapp.ui.screen.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rokt.demoapp.ui.common.BackButton
import com.rokt.demoapp.ui.common.HeaderTextButton
import com.rokt.demoapp.ui.screen.custom.CustomCheckoutDestinations.ConfirmationScreen
import com.rokt.demoapp.ui.screen.custom.accountdetails.AccountDetails
import com.rokt.demoapp.ui.screen.custom.accountdetails.AccountDetailsScreen
import com.rokt.demoapp.ui.screen.custom.confirmation.ConfirmationScreen
import com.rokt.demoapp.ui.screen.custom.customerdetails.CustomerDetailsScreen
import com.rokt.roktdemo.ui.common.RoktHeader
import kotlinx.serialization.json.Json

@Composable
fun CustomCheckoutPage(backPressed: () -> Unit) {
    val navController = rememberNavController()
    val actions = CustomCheckoutActions(navController, backPressed)
    Column(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        RoktHeader {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BackButton(backPressed = actions.backPressed)
                HeaderTextButton("EXIT", { backPressed.invoke() })
            }
        }

        NavHost(
            navController = navController,
            startDestination = CustomCheckoutDestinations.AccountDetails,
        ) {
            composable(CustomCheckoutDestinations.AccountDetails) {
                AccountDetailsScreen { actions.navigateToCustomerDetails(it) }
            }
            composable(
                route = CustomCheckoutDestinations.CustomerDetails,
                arguments = listOf(navArgument("accountDetails") { type = NavType.StringType }),
            ) { backStackEntry ->
                val accountDetailsJson = backStackEntry.arguments?.getString("accountDetails")
                val accountDetails: AccountDetails = Json.decodeFromString(accountDetailsJson!!)
                CustomerDetailsScreen(accountDetails) { accountDetails, customerDetails ->
                    actions.navigateToConfirmationScreen(accountDetails, customerDetails)
                }
            }
            composable(
                ConfirmationScreen,
                arguments = listOf(
                    navArgument("accountDetails") { type = NavType.StringType },
                    navArgument("customerDetails") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val accountDetailsJson = backStackEntry.arguments?.getString("accountDetails")
                val accountDetails: AccountDetails = Json.decodeFromString(accountDetailsJson!!)

                val customerDetailsJson = backStackEntry.arguments?.getString("customerDetails")
                val customerDetails: Map<String, String> = Json.decodeFromString(customerDetailsJson!!)
                ConfirmationScreen(accountDetails, customerDetails)
            }
        }
    }
}
