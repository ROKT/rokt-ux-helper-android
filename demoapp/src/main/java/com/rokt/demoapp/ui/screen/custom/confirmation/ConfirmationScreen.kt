package com.rokt.demoapp.ui.screen.custom.confirmation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.common.DefaultSpace
import com.rokt.demoapp.ui.common.LoadingPage
import com.rokt.demoapp.ui.common.MediumSpace
import com.rokt.demoapp.ui.common.SmallSpace
import com.rokt.demoapp.ui.common.SubHeading
import com.rokt.demoapp.ui.common.XSmallSpace
import com.rokt.demoapp.ui.common.error.RoktError
import com.rokt.demoapp.ui.screen.custom.accountdetails.AccountDetails
import com.rokt.demoapp.ui.theme.BorderColor
import com.rokt.demoapp.ui.theme.DefaultFontFamily
import com.rokt.demoapp.ui.theme.TextLight
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUx
import com.rokt.roktux.RoktUxConfig
import com.rokt.roktux.imagehandler.NetworkStrategy

@Composable
fun ConfirmationScreen(
    accountDetails: AccountDetails,
    customerAttributes: Map<String, String>,
    viewModel: ConfirmationViewModel = hiltViewModel(),
) {
    val scrollState = rememberScrollState(0)
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val integrationInfo = remember {
        RoktUx.getIntegrationConfig(context).toJsonString()
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_tick_confirmation_page),
            contentDescription = stringResource(R.string.content_description_tick_icon),
        )
        DefaultSpace()
        SubHeading(stringResource(R.string.confirmation_heading_text), 24)
        XSmallSpace()
        ConfirmationText(text = stringResource(R.string.text_order_numbr))
        MediumSpace()
        when {
            state.value.loading -> {
                LoadingPage()
            }

            state.value.hasData -> {
                RoktLayout(
                    experienceResponse = state.value.data!!.experienceResponse,
                    location = state.value.data!!.location,
                    onUxEvent = { println("RoktEvent: UxEvent Received $it") },
                    onPlatformEvent = { println("RoktEvent: onPlatformEvent received $it") },
                    roktUxConfig = RoktUxConfig.builder()
                        .imageHandlingStrategy(NetworkStrategy()).build(),
                )
            }

            else -> {
                RoktError(errorType = state.value.error)
            }
        }
        MediumSpace()
        OrderSummary()
        MediumSpace()
        CustomerDetails()
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.init(integrationInfo, accountDetails, customerAttributes)
    }
}

@Composable
private fun SectionHeading(text: String) {
    BoldText(text, Modifier.padding(15.dp))
    Divider()
}

@Composable
private fun OrderSummary() {
    Column(
        Modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.onSurface)
            .background(Color.White),
    ) {
        SectionHeading(
            text = stringResource(R.string.text_order_summary),
        )
        Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ConfirmationText(
                        stringResource(R.string.text_basic_tshirt),
                    )
                    ConfirmationText(
                        stringResource(R.string.text_basic_tshirt_price),
                    )
                }
                Text(
                    stringResource(R.string.text_medium),
                    Modifier.padding(bottom = 16.dp),
                    fontSize = 12.sp,
                    color = TextLight,
                    fontFamily = DefaultFontFamily,
                )
            }
            Divider()
            SmallSpace()
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ConfirmationText(
                    stringResource(R.string.text_subtotal),
                )
                ConfirmationText(
                    stringResource(R.string.text_basic_tshirt_price),
                )
            }
            SmallSpace()
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ConfirmationText(
                    stringResource(R.string.text_shipping),
                )
                ConfirmationText(
                    stringResource(R.string.text_shipping_price),
                )
            }
            SmallSpace()
            Divider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BoldText(
                    stringResource(R.string.text_total),
                    Modifier.padding(top = 16.dp),
                )
                BoldText(
                    stringResource(R.string.text_basic_tshirt_price),
                    Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun BoldText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.secondary,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    )
}

@Composable
private fun BoldTextSmall(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.secondary,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    )
}

@Composable
private fun CustomerDetails() {
    Column(
        Modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.onSurface)
            .background(Color.White),
    ) {
        SectionHeading(text = stringResource(R.string.text_customer_details))
        MediumSpace()
        Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
            BoldTextSmall(stringResource(R.string.text_email))
            XSmallSpace()
            ConfirmationText(stringResource(R.string.text_email_value))
            MediumSpace()

            BoldTextSmall(stringResource(R.string.text_shipping_address))
            XSmallSpace()
            ConfirmationText(
                stringResource(R.string.text_shipping_address_value),
            )
            MediumSpace()
            BoldTextSmall(stringResource(R.string.text_billing_address))
            XSmallSpace()
            ConfirmationText(
                stringResource(R.string.text_shipping_address_value),
            )

            MediumSpace()
            BoldTextSmall(stringResource(R.string.text_payment_method))
            XSmallSpace()
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .width(34.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                        .height(21.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_apple),
                        contentDescription = stringResource(R.string.content_description_apple_logo),
                        Modifier
                            .align(Alignment.Center),
                    )
                }
                ConfirmationText(
                    text = stringResource(id = R.string.text_payment_details),
                    Modifier.padding(start = 5.dp),
                )
            }
            MediumSpace()
            BoldTextSmall(stringResource(R.string.text_shipping_method))
            XSmallSpace()
            ConfirmationText(stringResource(R.string.text_shipping_duration))
            MediumSpace()
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.image_map),
                contentDescription = stringResource(R.string.content_description_map_image),
            )
            MediumSpace()
        }
    }
}

@Composable
private fun ConfirmationText(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        fontSize = 16.sp,
        color = TextLight,
        fontFamily = DefaultFontFamily,
    )
}

@Composable
private fun Divider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.5.dp)
            .background(color = BorderColor),
    )
}
