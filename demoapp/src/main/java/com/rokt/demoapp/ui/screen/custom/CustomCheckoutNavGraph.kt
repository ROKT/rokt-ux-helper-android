package com.rokt.demoapp.ui.screen.custom

import androidx.navigation.NavController
import com.rokt.demoapp.ui.screen.custom.accountdetails.AccountDetails
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Destinations in the Custom Checkout page.
 */
object CustomCheckoutDestinations {
    const val AccountDetails = "AccountDetails"
    const val CustomerDetails = "CustomerDetails/{accountDetails}"
    const val ConfirmationScreen = "ConfirmationScreen/{accountDetails}/{customerDetails}"
}

/**
 * Models the navigation actions in the Custom Checkout Page.
 */
class CustomCheckoutActions(
    navController: NavController,
    backPressed: () -> Unit,
) {

    val navigateToCustomerDetails: (accountDetails: AccountDetails) -> Unit = {
        val accountDetails = Json.encodeToString(it)
        navController.navigate(CustomCheckoutDestinations.CustomerDetails.replace("{accountDetails}", accountDetails))
    }

    val navigateToConfirmationScreen: (accountDetails: AccountDetails, customerDetails: Map<String, String>) -> Unit =
        { accountDetails, customerDetails ->
            val accountDetails = Json.encodeToString(accountDetails)
            val customerDetails = Json.encodeToString(customerDetails)
            navController.navigate(
                CustomCheckoutDestinations.ConfirmationScreen.replace(
                    "{accountDetails}",
                    accountDetails,
                ).replace("{customerDetails}", customerDetails),
            )
        }

    val backPressed: () -> Unit = {
        if (navController.navigateUp().not()) {
            backPressed.invoke()
        }
    }
}
