package com.rokt.demoapp.ui.screen.custom.accountdetails

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokt.demoapp.ui.common.EditableField
import com.rokt.demoapp.ui.common.createEditableField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(AccountDetailsViewState(initialized = false))
    val state: MutableStateFlow<AccountDetailsViewState>
        get() = _state

    private val account = MutableStateFlow(
        AccountDetailsViewData("", "", "", "", ""),
    )

    private val accountValidationState =
        MutableStateFlow(ValidationState(fieldStatus = ValidationStatus.NONE))

    fun init(accountDetails: AccountDetails = getDefaultAccountDetails()) {
        if (state.value.initialized.not()) {
            // Set default values
            account.value.accountId = accountDetails.accountID
            account.value.viewName = accountDetails.viewName
            account.value.placementLocation1 = accountDetails.placementLocation1
            account.value.placementLocation2 = accountDetails.placementLocation2
            // Observe changes to default values
            initState()
        }
    }

    private fun getDefaultAccountDetails(): AccountDetails = AccountDetails(
        accountID = "2754655826098840951",
        viewName = "readmoreLayout",
        placementLocation1 = "Location1",
        placementLocation2 = "Location2",
    )

    private fun initState() {
        viewModelScope.launch {
            combine(
                account,
                accountValidationState,
            ) { accountDetail, accountValidation ->
                AccountDetailsViewState(
                    accountId = createEditableField(
                        text = accountDetail.accountId,
                        onFieldEdited = {
                            account.value = account.value.copy(accountId = it)
                            onAccountFieldEdited()
                        },
                        accountValidation.fieldErrorMessage,
                    ),
                    viewName = createEditableField(
                        text = accountDetail.viewName,
                        onFieldEdited = {
                            account.value = account.value.copy(viewName = it)
                        },
                    ),
                    placementLocation1 = createEditableField(
                        text = accountDetail.placementLocation1,
                        onFieldEdited = {
                            account.value = account.value.copy(placementLocation1 = it)
                        },
                    ),
                    placementLocation2 = createEditableField(
                        text = accountDetail.placementLocation2,
                        onFieldEdited = {
                            account.value = account.value.copy(placementLocation2 = it)
                        },
                    ),
                    formValidated = accountValidationState.value.fieldStatus == ValidationStatus.VALID,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun continueButtonPressed() {
        accountValidationState.value = validateAccountId(account.value.accountId)
    }

    fun onNavigatedAway() {
        // Reset validationState, so when we return the fields can be modified
        accountValidationState.value = ValidationState(ValidationStatus.NONE)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun onAccountFieldEdited() {
        accountValidationState.value = ValidationState(ValidationStatus.NONE)
    }

    private fun validateAccountId(accountId: String): ValidationState = if (accountId.isEmpty()) {
        ValidationState(
            fieldStatus = ValidationStatus.INVALID,
            "Account Id can't be empty",
        )
    } else {
        ValidationState(fieldStatus = ValidationStatus.VALID)
    }
}

data class AccountDetailsViewState(
    val accountId: EditableField = EditableField(),
    val viewName: EditableField = EditableField(),
    val placementLocation1: EditableField = EditableField(),
    val placementLocation2: EditableField = EditableField(),
    val formValidated: Boolean = false,
    val initialized: Boolean = true,
)

data class AccountDetailsViewData(
    var accountId: String,
    var viewName: String,
    var placementLocation1: String,
    var placementLocation2: String,
    var password: String,
)

data class ValidationState(val fieldStatus: ValidationStatus, val fieldErrorMessage: String = "")

enum class ValidationStatus {
    NONE,
    VALID,
    INVALID,
}

@Serializable
data class AccountDetails(
    val accountID: String,
    val viewName: String,
    val placementLocation1: String,
    val placementLocation2: String,
)
