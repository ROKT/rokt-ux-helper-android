package com.rokt.demoapp.ui.screen.custom.accountdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.common.ButtonLight
import com.rokt.demoapp.ui.common.ContentText
import com.rokt.demoapp.ui.common.EditableField
import com.rokt.demoapp.ui.common.MediumSpace
import com.rokt.demoapp.ui.common.RoktTextField
import com.rokt.demoapp.ui.common.ScreenHeader
import com.rokt.demoapp.ui.common.SmallSpace
import com.rokt.demoapp.ui.common.XSmallSpace
import com.rokt.demoapp.ui.theme.DefaultFontFamily
import com.rokt.demoapp.ui.theme.ErrorColor
import com.rokt.demoapp.ui.theme.TextLight

@Composable
internal fun AccountDetailsScreen(navigateToCustomerDetails: (AccountDetails) -> Unit) {
    val viewModel: AccountDetailsViewModel = hiltViewModel()

    val state = viewModel.state.collectAsState()
    val scroll = rememberScrollState(0)

    viewModel.init()
    if (state.value.formValidated) {
        viewModel.onNavigatedAway()
        navigateToCustomerDetails(
            AccountDetails(
                state.value.accountId.text,
                state.value.viewName.text,
                state.value.placementLocation1.text,
                state.value.placementLocation2.text,
            ),
        )
    }

    val onContinueButtonPress = {
        viewModel.continueButtonPressed()
    }
    AccountDetailsScreenContent(
        scroll,
        onContinueButtonPress,
        state.value.accountId,
        state.value.viewName,
        state.value.placementLocation1,
        state.value.placementLocation2,
    )
}

@Composable
private fun AccountDetailsScreenContent(
    scrollState: ScrollState,
    continueButtonPressed: () -> Unit,
    accountId: EditableField,
    viewName: EditableField,
    placementLocation1: EditableField,
    placementLocation2: EditableField,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .verticalScroll(scrollState)
            .systemBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.account_details_step_count_text),
            fontSize = 16.sp,
            color = TextLight,
        )
        SmallSpace()
        ScreenHeader(text = stringResource(R.string.label_account_details))
        SmallSpace()
        ContentText(stringResource(id = R.string.text_account_details_description))
        XSmallSpace()
        ErrorTextField(
            stringResource(R.string.label_account_id),
            accountId.text,
            accountId.onValueChanged,
            accountId.errorText,
        )
        RoktTextField(
            stringResource(R.string.label_view_name),
            viewName.text,
            viewName.onValueChanged,
        )
        RoktTextField(
            stringResource(R.string.label_location_1),
            placementLocation1.text,
            placementLocation1.onValueChanged,
        )
        RoktTextField(
            stringResource(R.string.label_location_2),
            placementLocation2.text,
            placementLocation2.onValueChanged,
        )
        MediumSpace()
        ButtonLight(text = stringResource(R.string.button_continue)) {
            continueButtonPressed.invoke()
        }
    }
}

@Composable
private fun ErrorTextField(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    errorText: String,
    isPassword: Boolean = false,
) {
    Column(Modifier.fillMaxWidth()) {
        RoktTextField(
            label,
            text,
            onValueChange,
            errorText,
            isPassword,
        )

        if (errorText.isNotEmpty()) {
            ErrorMessage(errorText)
        }
    }
}

@Composable
fun ErrorMessage(text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = stringResource(R.string.content_description_exclamation),
        )
        Text(
            modifier = Modifier.padding(start = 5.dp),
            text = text,
            color = ErrorColor,
            fontFamily = DefaultFontFamily,
            fontSize = 14.sp,
        )
    }
}
