package com.rokt.demoapp.ui.screen.custom.customerdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.common.ButtonDark
import com.rokt.demoapp.ui.common.ContentText
import com.rokt.demoapp.ui.common.DefaultSpace
import com.rokt.demoapp.ui.common.DropDownList
import com.rokt.demoapp.ui.common.EditableField
import com.rokt.demoapp.ui.common.EditableFieldSet
import com.rokt.demoapp.ui.common.HeaderTextButton
import com.rokt.demoapp.ui.common.RoktTextField
import com.rokt.demoapp.ui.common.ScreenHeader
import com.rokt.demoapp.ui.common.SmallSpace
import com.rokt.demoapp.ui.common.XSmallSpace
import com.rokt.demoapp.ui.screen.custom.accountdetails.AccountDetails
import com.rokt.demoapp.ui.theme.DefaultFontFamily
import com.rokt.demoapp.ui.theme.TextLight
import java.util.HashMap

@Composable
fun CustomerDetailsScreen(
    accountDetails: AccountDetails,
    navigateToConfirmationScreen: (AccountDetails, HashMap<String, String>) -> Unit,
) {
    val scroll = rememberScrollState(0)
    val viewModel: CustomerDetailsViewModel = hiltViewModel()
    viewModel.init()

    val state = viewModel.state.collectAsState()
    val launchDemoButtonPressed = {
        navigateToConfirmationScreen(accountDetails, viewModel.getCustomerDetails())
    }

    CustomerDetailsScreenContent(
        scroll,
        state.value.showAdvancedOptions,
        state.value.advancedOptions,
        viewModel::onToggleAdvancedOptions,
        launchDemoButtonPressed::invoke,
        state.value.selectedCountry,
        viewModel::onCountrySelected,
        state.value.selectedState,
        state.value.postcode,
        state.value.countryList,
    )
}

@Composable
private fun CustomerDetailsScreenContent(
    scrollState: ScrollState,
    showAdvancedOptions: Boolean,
    advancedOptions: List<EditableFieldSet>,
    onToggleAdvancedOptions: () -> Unit,
    navigateToNextScreen: () -> Unit,
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    selectedState: EditableField,
    postcode: EditableField,
    countryList: List<String>,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .verticalScroll(scrollState)
            .systemBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.customer_screen_step_label),
            fontSize = 16.sp,
            color = TextLight,
        )
        SmallSpace()
        ScreenHeader(text = stringResource(R.string.customer_details_screen_title))
        SmallSpace()
        ContentText(stringResource(R.string.customer_details_screen_description))
        XSmallSpace()
        CountrySelection(countryList, selectedCountry, onCountrySelected)
        RoktTextField(
            stringResource(R.string.textfield_label_state),
            selectedState.text,
            selectedState.onValueChanged,
        )
        RoktTextField(
            stringResource(R.string.textfield_label_postcode),
            postcode.text,
            postcode.onValueChanged,
        )
        AdvancedOptionsToggle(onToggleAdvancedOptions)
        DefaultSpace()
        if (showAdvancedOptions) {
            AdvancedOptionsContent(advancedOptions)
        }

        Spacer(modifier = Modifier.weight(1f))
        ButtonDark(text = stringResource(R.string.launch_demo_button_text), navigateToNextScreen)
    }
}

@Composable
private fun CountrySelection(countryList: List<String>, selectedCountry: String, onCountrySelected: (String) -> Unit) {
    val isOpen = remember { mutableStateOf(false) } // initial value
    val openCloseOfDropDownList: (Boolean) -> Unit = {
        isOpen.value = it
    }

    Box {
        Column {
            RoktTextField(
                stringResource(R.string.label_country),
                selectedCountry,
                {},
            )
            DropDownList(
                requestToOpen = isOpen.value,
                list = countryList,
                openCloseOfDropDownList,
                onCountrySelected,
            )
        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { isOpen.value = true },
                ),
        )
    }
}

@Composable
private fun AdvancedOptionsToggle(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
    ) {
        HeaderTextButton(
            stringResource(R.string.advanced_options_toggle_text),
            { onClick.invoke() },
            color = MaterialTheme.colorScheme.secondary,
        )
        Image(
            modifier = Modifier.padding(start = 6.dp),
            painter = painterResource(id = R.drawable.ic_drop_down),
            contentDescription = stringResource(
                R.string.drop_down_content_description,
            ),
        )
    }
}

@Composable
private fun AdvancedOptionsContent(fields: List<EditableFieldSet>) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        Text(
            modifier = Modifier.padding(top = 15.dp, bottom = 24.dp),
            text = stringResource(R.string.advanced_options_description),
            fontSize = 12.sp,
            fontFamily = DefaultFontFamily,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
        )

        fields.forEach { fieldPair ->
            Row(Modifier.fillMaxWidth()) {
                RoktTextField(
                    stringResource(R.string.label_attribute_name),
                    fieldPair.key,
                    fieldPair.onKeyChanged,
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(1F)
                        .padding(end = 4.dp),
                )

                RoktTextField(
                    stringResource(R.string.label_attribute_value),
                    fieldPair.value,
                    fieldPair.onValueChanged,
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(1F)
                        .padding(start = 4.dp),
                )
            }
        }
    }
}
