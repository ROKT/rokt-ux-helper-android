package com.rokt.roktux.viewmodel

import com.rokt.core.testutils.BaseViewModelTest
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.modelmapper.uimodel.Action
import com.rokt.modelmapper.uimodel.LayoutSettings
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.modelmapper.uimodel.OptionsModel
import com.rokt.modelmapper.uimodel.PaymentMethod
import com.rokt.modelmapper.uimodel.PlacementContextModel
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.modelmapper.uimodel.TransactionData
import com.rokt.network.model.PaymentProvider
import com.rokt.roktux.RoktViewState
import com.rokt.roktux.event.DevicePayResult
import com.rokt.roktux.event.EventType
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent
import com.rokt.roktux.validation.ValidationCoordinator
import com.rokt.roktux.validation.ValidationStatus
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.LayoutViewModel
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoktLayoutViewModelTest : BaseViewModelTest() {
    private val ioDispatcher = UnconfinedTestDispatcher()
    private val mapper: ModelMapper = mockk(relaxed = true) {
        coEvery { transformResponse() } returns mockk(relaxed = true)
        every { getSavedExperience() } returns mockk(relaxed = true) {
            every { sessionId } returns "sessionId"
            every { placementContext } returns PlacementContextModel("pageInstanceGuid", "layout_token")
            every { plugins } returns persistentListOf(
                mockk(relaxed = true) {
                    every { id } returns "pluginId"
                    every { name } returns "pluginName"
                    every { targetElementSelector } returns "location1"
                    every { outerLayoutSchema } returns mockk(relaxed = true)
                    every { breakpoint } returns mockk(relaxed = true)
                    every { instanceGuid } returns "pluginInstanceGuid"
                    every { slots } returns persistentListOf(
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid"
                                }
                            }
                        },
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid1"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid1"
                                }
                            }
                        },
                    )
                    every { settings } returns LayoutSettings(closeOnComplete = true)
                },
            )
        }
    }

    private val uxEvent: (RoktUxEvent) -> Unit = mockk(relaxed = true)
    private val platformEvent: (List<RoktPlatformEvent>) -> Unit = mockk(relaxed = true)
    private val viewStateChange: (RoktViewState) -> Unit = mockk(relaxed = true)
    private lateinit var layoutViewModel: LayoutViewModel

    @Before
    fun setup() {
        initialize()
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
    }

    private fun initialize(
        handleUrlByApp: Boolean = true,
        validationCoordinator: ValidationCoordinator = ValidationCoordinator(),
    ) {
        layoutViewModel = LayoutViewModel(
            location = "location1",
            startTimeStamp = System.currentTimeMillis(),
            uxEvent = uxEvent,
            platformEvent = platformEvent,
            modelMapper = mapper,
            ioDispatcher = ioDispatcher,
            mainDispatcher = ioDispatcher,
            handleUrlByApp = handleUrlByApp,
            currentOffer = 0,
            viewStateChange = viewStateChange,
            customStates = mapOf(),
            offerCustomStates = mapOf(),
            validationCoordinator = validationCoordinator,
            edgeToEdgeDisplay = false,
        )
    }

    @Test
    fun `LayoutReady Event should send LayoutReady UxEvent and SignalLoadComplete platform event`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutReady)
        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.LayoutReady("pluginId"))
        }
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anyMatch { it.eventType == EventType.SignalLoadComplete }
                },
            )
        }
    }

    @Test
    fun `LayoutInteractive Event should send LayoutInteractive UxEvent`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInteractive)
        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.LayoutInteractive("pluginId"))
        }
    }

    @Test
    fun `ResponseOptionSelected Event should send UxEvents`() = runTest {
        // Arrange
        val responseOptionProperties = HMap().apply {
            this[TypedKey<Boolean>("isPositive")] = true
            this[TypedKey<Action>("action")] = Action.CaptureOnly
            this[TypedKey<SignalType>("signalType")] = SignalType.SignalResponse
            this[TypedKey<String>("instanceGuid")] = "responseInstanceGuid"
        }
        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.ResponseOptionSelected(
                1,
                OpenLinks.Internally,
                responseOptionProperties,
                true,
            ),
        )
        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.OfferEngagement("pluginId"))
            uxEvent.invoke(RoktUxEvent.PositiveEngagement("pluginId"))
        }
    }

    @Test
    fun `SetCurrentOffer Event past the total offers should send not LayoutCompleted UxEvent and SignalDismissal platformEvent when closeOnComplete is false`() = runTest {
        // Arrange
        every { mapper.getSavedExperience() } returns mockk(relaxed = true) {
            every { plugins } returns persistentListOf(
                mockk(relaxed = true) {
                    every { id } returns "pluginId"
                    every { settings } returns LayoutSettings(closeOnComplete = false)
                    every { slots } returns persistentListOf(
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid"
                                }
                            }
                        },
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid1"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid1"
                                }
                            }
                        },
                    )
                },
            )
        }

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.SetCurrentOffer(2))

        // Assert
        verify(exactly = 0) {
            uxEvent.invoke(RoktUxEvent.LayoutCompleted("pluginId"))
            platformEvent.invoke(
                match { event ->
                    event[0].eventType == EventType.SignalDismissal
                },
            )
            viewStateChange.invoke(
                withArg {
                    assertEquals(it.offerIndex, 2)
                    assertTrue(it.pluginDismissed)
                },
            )
        }
    }

    @Test
    fun `LayoutInitialised Event when experienceModel has error should end LayoutFailure UxEvent but not diagnostic event`() = runTest {
        // Arrange
        every { mapper.transformResponse() } returns Result.failure(IllegalAccessException("no access"))

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)

        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.LayoutFailure())
        }

        verify(exactly = 0) {
            platformEvent.invoke(
                match { event ->
                    event[0].eventType == EventType.SignalSdkDiagnostic
                },
            )
        }
    }

    @Test
    fun `handleError should not send error if experienceModel is initialized and useDiagnostics is false`() = runTest {
        // Arrange
        every { mapper.getSavedExperience() } returns mockk(relaxed = true) {
            every { options } returns OptionsModel(useDiagnosticEvents = false)
        }

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
        layoutViewModel.handleError(IllegalAccessException("no access"))

        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.LayoutFailure())
        }
    }

    @Test
    fun `UrlSelected Event should send OpenUrlInternal UxEvent when no value set for handleUrlByApp`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.UrlSelected("url", OpenLinks.Internally))

        // Assert
        verify {
            uxEvent.invoke(
                match { event ->
                    event::class.java == RoktUxEvent.OpenUrl::class.java &&
                        (event as RoktUxEvent.OpenUrl).url == "url"
                },
            )
        }
    }

    @Test
    fun `UrlSelected Event should send OpenUrl Passthrough UxEvent when no value set for handleUrlByApp`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.UrlSelected("url", OpenLinks.Passthrough))

        // Assert
        verify {
            uxEvent.invoke(
                match { event ->
                    event::class.java == RoktUxEvent.OpenUrl::class.java &&
                        (event as RoktUxEvent.OpenUrl).url == "url" &&
                        event.type == OpenLinks.Passthrough
                },
            )
        }
    }

    @Test
    fun `UrlSelected Event should send OpenUrl Passthrough UxEvent when handleUrlByApp set to false`() = runTest {
        // Arrange
        initialize(handleUrlByApp = false)
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.UrlSelected("url", OpenLinks.Passthrough))

        // Assert
        verify {
            uxEvent.invoke(
                match { event ->
                    event::class.java == RoktUxEvent.OpenUrl::class.java &&
                        (event as RoktUxEvent.OpenUrl).url == "url" &&
                        event.type == OpenLinks.Passthrough &&
                        event.layoutId == "pluginId"
                },
            )
        }
    }

    @Test
    fun `UrlSelected Event should set effect OpenUrlInternal when UrlSelected event is passed with Internally and  handleUrlByApp set to false`() = runTest {
        // Arrange
        initialize(handleUrlByApp = false)

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.UrlSelected("url", OpenLinks.Internally))

        // Assert

        val effect = layoutViewModel.effect.first()
        assertThat(effect)
            .matches { (it as LayoutContract.LayoutEffect.OpenUrlInternal).url == "url" }
    }

    @Test
    fun `UrlSelected Event should set effect OpenUrlExternally when UrlSelected event is passed with Externally and  handleUrlByApp set to false`() = runTest {
        // Arrange
        initialize(handleUrlByApp = false)

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.UrlSelected("url", OpenLinks.Externally))

        // Assert

        val effect = layoutViewModel.effect.first()
        assertThat(effect)
            .matches { (it as LayoutContract.LayoutEffect.OpenUrlExternal).url == "url" }
    }

    @Test
    fun `SetOfferCustomState Event should update custom state and propagate the event`() = runTest {
        // Arrange
        val key = "key"
        val value = 1

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.SetOfferCustomState(0, mapOf(key to value)))

        // Assert
        verify {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates).containsEntry("0", mapOf(key to value))
                },
            )
        }
    }

    @Test
    fun `SetCustomState Event should update custom state and propagate the event`() = runTest {
        // Arrange
        val key = "key"
        val value = 1

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.SetCustomState(key, value))

        // Assert
        verify {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.customStates).containsEntry(key, value)
                    assertThat(state.offerCustomStates).isEmpty()
                },
            )
        }
    }

    @Test
    fun `CartItemDevicePaySelected sends event with sale price and platform signal`() = runTest {
        // Arrange
        val transactionData = TransactionData(
            paymentType = "GooglePay",
            supportedPaymentMethods = listOf(PaymentMethod("GOOGLE_PAY")),
        )
        clearMocks(uxEvent, platformEvent, viewStateChange)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemDevicePaySelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                paymentProvider = PaymentProvider.GooglePay,
                transactionData = transactionData,
                validatorFieldKeys = emptyList(),
            ),
        )

        // Assert
        val event = captureDevicePayEvent()
        assertThat(event.layoutId).isEqualTo("pluginId")
        assertThat(event.name).isEqualTo("Everyday sneakers")
        assertThat(event.cartItemId).isEqualTo("cart-item-1")
        assertThat(event.catalogItemId).isEqualTo("catalog-item-1")
        assertThat(event.currency).isEqualTo("USD")
        assertThat(event.description).isEqualTo("Lightweight daily shoes")
        assertThat(event.linkedProductId).isEqualTo("linked-product-1")
        assertThat(event.providerData).isEqualTo("{\"merchant\":\"rokt\"}")
        assertThat(event.totalPrice).isEqualTo(79.99)
        assertThat(event.unitPrice).isEqualTo(79.99)
        assertThat(event.paymentProvider).isEqualTo(PaymentProvider.GooglePay)
        assertThat(event.transactionData).isEqualTo(transactionData)
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anyMatch {
                        it.eventType == EventType.SignalCartItemInstantPurchaseInitiated &&
                            it.parentGuid == "catalog-instance-guid-1" &&
                            it.eventData?.get("totalPrice") == "79.99" &&
                            it.eventData?.get("unitPrice") == "79.99"
                    }
                },
            )
        }
    }

    @Test
    fun `CartItemDevicePay callback updates payment result custom state`() = runTest {
        // Arrange
        clearMocks(uxEvent, viewStateChange)
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemDevicePaySelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                paymentProvider = PaymentProvider.GooglePay,
                transactionData = null,
                validatorFieldKeys = emptyList(),
            ),
        )
        val event = captureDevicePayEvent()

        // Act
        clearMocks(viewStateChange)
        event.onResult(DevicePayResult.Success)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", 1)
                },
            )
        }

        // Act
        clearMocks(viewStateChange)
        event.onResult(DevicePayResult.Failure)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", -1)
                },
            )
        }

        // Act
        clearMocks(viewStateChange)
        event.onResult(DevicePayResult.Retry)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", -1)
                },
            )
        }
    }

    @Test
    fun `CartItemDevicePay pending confirmation updates device pay state`() = runTest {
        // Arrange
        clearMocks(uxEvent, viewStateChange)
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemDevicePaySelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                paymentProvider = PaymentProvider.GooglePay,
                transactionData = null,
                validatorFieldKeys = emptyList(),
            ),
        )
        val event = captureDevicePayEvent()

        // Act
        clearMocks(viewStateChange)
        event.onResult(DevicePayResult.PendingConfirmation(mapOf("confirmationRef" to "confirmation-123")))

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("devicePayState", 1)
                },
            )
        }
    }

    @Test
    fun `CartItemDevicePaySelected does not emit event when validation fails`() = runTest {
        // Arrange
        val validationCoordinator = ValidationCoordinator()
        validationCoordinator.registerField(
            key = "dropDownSelection",
            owner = this,
            validation = { ValidationStatus.INVALID },
            onStatusChange = {},
        )
        initialize(validationCoordinator = validationCoordinator)
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
        clearMocks(uxEvent)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemDevicePaySelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                paymentProvider = PaymentProvider.GooglePay,
                transactionData = null,
                validatorFieldKeys = listOf("dropDownSelection"),
            ),
        )

        // Assert
        verify(exactly = 0, timeout = 500) {
            uxEvent.invoke(match { event -> event is RoktUxEvent.CartItemDevicePay })
        }
    }

    @Test
    fun `FirstOfferLoaded should send the RoktPlatformEvent with SignalImpression for the layout and required metadata`() {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.FirstOfferLoaded)

        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anyMatch {
                        it.eventType == EventType.SignalImpression &&
                            it.parentGuid == "pluginInstanceGuid" &&
                            it.metadata.size == 5 &&
                            it.metadata.any { data -> data.name == "pageRenderEngine" && data.value == "Layouts" } &&
                            it.metadata.any { data -> data.name == "pageSignalLoadStart" } &&
                            it.metadata.any { data -> data.name == "pageSignalLoadComplete" }
                    }
                },
            )
        }
    }

    @Test
    fun `UiException Event should send SignalSdkDiagnostic PlatformEvent`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.UiException(IllegalAccessException("no access"), true))

        // Assert
        verify(exactly = 0) {
            platformEvent.invoke(
                match { event ->
                    event[0].eventType == EventType.SignalSdkDiagnostic
                },
            )
        }
    }

    private fun captureDevicePayEvent(): RoktUxEvent.CartItemDevicePay {
        val eventSlot = slot<RoktUxEvent>()
        verify(timeout = 2000) {
            uxEvent.invoke(capture(eventSlot))
        }
        return eventSlot.captured as RoktUxEvent.CartItemDevicePay
    }

    private fun createCatalogItemProperties(
        price: Double = 79.99,
        originalPrice: Double = 99.99,
    ): HMap = HMap().apply {
        set(TypedKey<String>("instanceGuid"), "catalog-instance-guid-1")
        set(TypedKey<String>("title"), "Everyday sneakers")
        set(TypedKey<String>("cartItemId"), "cart-item-1")
        set(TypedKey<String>("catalogItemId"), "catalog-item-1")
        set(TypedKey<String>("currency"), "USD")
        set(TypedKey<String>("description"), "Lightweight daily shoes")
        set(TypedKey<String>("linkedProductId"), "linked-product-1")
        set(TypedKey<String>("providerData"), "{\"merchant\":\"rokt\"}")
        set(TypedKey<Double>("price"), price)
        set(TypedKey<Double>("originalPrice"), originalPrice)
    }
}
