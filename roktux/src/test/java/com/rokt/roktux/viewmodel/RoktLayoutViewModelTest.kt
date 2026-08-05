package com.rokt.roktux.viewmodel

import com.rokt.core.testutils.BaseViewModelTest
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.modelmapper.uimodel.Action
import com.rokt.modelmapper.uimodel.CatalogImageWrapperModel
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.LayoutSettings
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.modelmapper.uimodel.OptionsModel
import com.rokt.modelmapper.uimodel.OrderableWhenUiCondition
import com.rokt.modelmapper.uimodel.PaymentMethod
import com.rokt.modelmapper.uimodel.PlacementContextModel
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.modelmapper.uimodel.TransactionData
import com.rokt.modelmapper.uimodel.WhenUiPredicate
import com.rokt.network.model.PaymentProvider
import com.rokt.roktux.RoktViewState
import com.rokt.roktux.component.evaluatePredicates
import com.rokt.roktux.event.DevicePayResult
import com.rokt.roktux.event.EventType
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUserInteractionAction
import com.rokt.roktux.event.RoktUserInteractionContext
import com.rokt.roktux.event.RoktUxEvent
import com.rokt.roktux.state.LayoutRuntimeState
import com.rokt.roktux.validation.ValidationCoordinator
import com.rokt.roktux.validation.ValidationStatus
import com.rokt.roktux.viewmodel.base.BaseContract
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
                    every { token } returns "pluginToken"
                    every { slots } returns persistentListOf(
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid"
                            every { token } returns "slotToken"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid"
                                    every { token } returns "creativeToken"
                                }
                                every { catalogItems } returns persistentListOf(
                                    CatalogItemModel(
                                        properties = createCatalogItemProperties(),
                                        imageWrapper = CatalogImageWrapperModel(HMap()),
                                    ),
                                    CatalogItemModel(
                                        properties = createCatalogItemProperties(
                                            instanceGuid = "catalog-instance-guid-2",
                                            catalogItemId = "catalog-item-2",
                                            token = "catalog-token-2",
                                        ),
                                        imageWrapper = CatalogImageWrapperModel(HMap()),
                                    ),
                                )
                            }
                        },
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid1"
                            every { token } returns "slotToken1"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid1"
                                    every { token } returns "creativeToken1"
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
        domainStates: Map<String, Int> = emptyMap(),
        validationCoordinator: ValidationCoordinator = ValidationCoordinator(),
        completedDevicePayCartItemIds: Set<String> = emptySet(),
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
            domainStates = domainStates,
            validationCoordinator = validationCoordinator,
            edgeToEdgeDisplay = false,
            completedDevicePayCartItemIds = completedDevicePayCartItemIds,
        )
    }

    @Test
    fun `completedDevicePayCartItemIds seeds the matching offer into its post-purchase state on init`() = runTest {
        // Arrange: a fresh VM seeded with a completed purchase for the cart item in offer index 0,
        // simulating restore after the host was destroyed mid-checkout (no live device-pay event).
        clearMocks(viewStateChange)
        initialize(completedDevicePayCartItemIds = setOf("cart-item-1"))

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)

        // Assert: offer 0 is already in its paymentResult=1 (confirmation) state on first render.
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", 1)
                },
            )
        }
    }

    @Test
    fun `completedDevicePayCartItemIds does not seed any offer when the cart item is unknown`() = runTest {
        clearMocks(viewStateChange)
        initialize(completedDevicePayCartItemIds = setOf("not-a-real-cart-item"))

        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)

        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]?.get("paymentResult")).isNull()
                },
            )
        }
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
    fun `LayoutReady Event sends SignalLoadComplete platform event carrying the plugin token`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutReady)
        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { event ->
                        assertThat(event.eventType).isEqualTo(EventType.SignalLoadComplete)
                        assertThat(event.parentGuid).isEqualTo("pluginInstanceGuid")
                        assertThat(event.token).isEqualTo("pluginToken")
                    }
                },
            )
        }
    }

    @Test
    fun `ResponseOptionSelected sends SignalResponse platform event carrying the response option token`() = runTest {
        // Arrange
        val responseOptionProperties = HMap().apply {
            this[TypedKey<Action>("action")] = Action.CaptureOnly
            this[TypedKey<SignalType>("signalType")] = SignalType.SignalResponse
            this[TypedKey<String>("instanceGuid")] = "responseInstanceGuid"
            this[TypedKey<String>("token")] = "responseToken"
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
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { event ->
                        assertThat(event.eventType).isEqualTo(EventType.SignalResponse)
                        assertThat(event.parentGuid).isEqualTo("responseInstanceGuid")
                        assertThat(event.token).isEqualTo("responseToken")
                        assertThat(event.metadata.map { it.name }).doesNotContain("transformedTrafficURL")
                    }
                },
            )
        }
    }

    @Test
    fun `ResponseOptionSelected Url action sends SignalResponse with click destination URL`() = runTest {
        // Arrange
        val responseOptionProperties = HMap().apply {
            this[TypedKey<Action>("action")] = Action.Url
            this[TypedKey<SignalType>("signalType")] = SignalType.SignalResponse
            this[TypedKey<String>("instanceGuid")] = "responseInstanceGuid"
            this[TypedKey<String>("token")] = "responseToken"
            this[TypedKey<String>("url")] = "https://example.com/offer"
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
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { event ->
                        assertThat(event.eventType).isEqualTo(EventType.SignalResponse)
                        assertThat(event.parentGuid).isEqualTo("responseInstanceGuid")
                        assertThat(event.token).isEqualTo("responseToken")
                        assertThat(event.metadata).anySatisfy { metadata ->
                            assertThat(metadata.name).isEqualTo("transformedTrafficURL")
                            assertThat(metadata.value).isEqualTo("https://example.com/offer")
                        }
                    }
                },
            )
        }
    }

    @Test
    fun `ResponseOptionSelected Url action with empty URL omits click destination metadata`() = runTest {
        // Arrange
        val responseOptionProperties = HMap().apply {
            this[TypedKey<Action>("action")] = Action.Url
            this[TypedKey<SignalType>("signalType")] = SignalType.SignalResponse
            this[TypedKey<String>("instanceGuid")] = "responseInstanceGuid"
            this[TypedKey<String>("token")] = "responseToken"
            this[TypedKey<String>("url")] = ""
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
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { event ->
                        assertThat(event.eventType).isEqualTo(EventType.SignalResponse)
                        assertThat(event.parentGuid).isEqualTo("responseInstanceGuid")
                        assertThat(event.token).isEqualTo("responseToken")
                        assertThat(event.metadata.map { it.name }).doesNotContain("transformedTrafficURL")
                    }
                },
            )
        }
    }

    @Test
    fun `CartItemInstantPurchaseSelected sends lean initiated signal with ids and no event body`() = runTest {
        // Arrange
        clearMocks(uxEvent, platformEvent, viewStateChange)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemInstantPurchaseSelected(
                catalogItemModel = createCatalogItemProperties(),
            ),
        )

        // Assert — rich host UX callback unchanged
        verify(timeout = 2000) {
            uxEvent.invoke(
                match { event ->
                    event is RoktUxEvent.CartItemInstantPurchase &&
                        event.layoutId == "pluginId" &&
                        event.cartItemId == "cart-item-1" &&
                        event.catalogItemId == "catalog-item-1" &&
                        event.currency == "USD" &&
                        event.description == "Lightweight daily shoes" &&
                        event.linkedProductId == "linked-product-1" &&
                        event.totalPrice == 99.99 &&
                        event.unitPrice == 99.99 &&
                        event.quantity == 1
                },
            )
        }
        // Assert — platform Initiated matches iOS: ids only, no fat eventData / objectData
        verify(timeout = 2000) {
            platformEvent.invoke(
                match { events ->
                    events.any {
                        it.eventType == EventType.SignalCartItemInstantPurchaseInitiated &&
                            it.parentGuid == "catalog-instance-guid-1" &&
                            it.token == "catalog-token-1" &&
                            it.pageInstanceGuid == "pageInstanceGuid" &&
                            it.eventData == null &&
                            it.objectData == null
                    }
                },
            )
        }
    }

    @Test
    fun `CartItemForwardPaymentSelected sends platform event carrying the catalog item token`() = runTest {
        // Arrange
        clearMocks(uxEvent, platformEvent, viewStateChange)
        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemForwardPaymentSelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                transactionData = null,
            ),
        )
        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { event ->
                        assertThat(event.eventType).isEqualTo(EventType.SignalCartItemInstantPurchaseInitiated)
                        assertThat(event.parentGuid).isEqualTo("catalog-instance-guid-1")
                        assertThat(event.token).isEqualTo("catalog-token-1")
                        assertThat(event.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                    }
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
    fun `initial domain states seed offer state and view state`() = runTest {
        // Arrange
        clearMocks(viewStateChange)
        initialize(domainStates = mapOf("checkout" to 2))

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)

        // Assert
        val successState = layoutViewModel.viewState.first {
            it is BaseContract.BaseViewState.Success && it.value.offerUiState.domainStates["checkout"] == 2
        }
            as BaseContract.BaseViewState.Success
        assertThat(successState.value.offerUiState.domainStates).containsEntry("checkout", 2)
        verify {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.domainStates).containsEntry("checkout", 2)
                },
            )
        }
    }

    @Test
    fun `SetDomainState Event should update domain state and propagate the event`() = runTest {
        // Arrange
        clearMocks(viewStateChange)

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.SetDomainState("offerComplete", 1))

        // Assert
        val successState = layoutViewModel.viewState.first {
            it is BaseContract.BaseViewState.Success && it.value.offerUiState.domainStates["offerComplete"] == 1
        }
            as BaseContract.BaseViewState.Success
        assertThat(successState.value.offerUiState.domainStates).containsEntry("offerComplete", 1)
        verify {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.domainStates).containsEntry("offerComplete", 1)
                },
            )
        }
    }

    @Test
    fun `domain state updates affect when evaluation`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.SetDomainState("checkout", 2))

        // Assert
        val successState = layoutViewModel.viewState.first {
            it is BaseContract.BaseViewState.Success && it.value.offerUiState.domainStates["checkout"] == 2
        }
            as BaseContract.BaseViewState.Success
        val evaluationResult = evaluatePredicates(
            predicates = persistentListOf(
                WhenUiPredicate.DomainState(
                    condition = OrderableWhenUiCondition.IsAbove,
                    key = "checkout",
                    value = 1,
                ),
            ),
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = successState.value.offerUiState,
        )

        assertTrue(evaluationResult)
    }

    @Test
    fun `CartItemForwardPaymentSelected sends event with sale price transaction data and platform signal`() = runTest {
        // Arrange
        val transactionData = TransactionData(
            paymentType = "Card",
            supportedPaymentMethods = listOf(PaymentMethod("CARD")),
            isPartnerManagedPurchase = false,
            partnerPaymentReference = "partner-reference",
        )
        clearMocks(uxEvent, platformEvent, viewStateChange)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemForwardPaymentSelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                transactionData = transactionData,
            ),
        )

        // Assert
        val event = captureForwardPaymentEvent()
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
        assertThat(event.transactionData).isEqualTo(transactionData)
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anyMatch {
                        it.eventType == EventType.SignalCartItemInstantPurchaseInitiated &&
                            it.parentGuid == "catalog-instance-guid-1" &&
                            it.pageInstanceGuid == "pageInstanceGuid" &&
                            it.eventData?.get("totalPrice") == "79.99" &&
                            it.eventData?.get("unitPrice") == "79.99"
                    }
                },
            )
        }
        verify(exactly = 0, timeout = 500) {
            platformEvent.invoke(match { events -> events.any { it.eventType == EventType.SignalDismissal } })
        }
    }

    @Test
    fun `CartItemForwardPayment callback updates payment result custom state`() = runTest {
        // Arrange
        clearMocks(uxEvent, platformEvent, viewStateChange)
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemForwardPaymentSelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                transactionData = TransactionData(isPartnerManagedPurchase = false),
            ),
        )
        val event = captureForwardPaymentEvent()

        // Act
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Success)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", 1)
                },
            )
        }
        verify(exactly = 0, timeout = 500) {
            platformEvent.invoke(match { events -> events.containsTerminalInstantPurchaseEvent() })
        }

        // Act
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Failure)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", -1)
                },
            )
        }
        verify(exactly = 0, timeout = 500) {
            platformEvent.invoke(match { events -> events.containsTerminalInstantPurchaseEvent() })
        }

        // Act
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Retry)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", -1)
                },
            )
        }
        verify(exactly = 0, timeout = 500) {
            platformEvent.invoke(match { events -> events.containsTerminalInstantPurchaseEvent() })
        }
    }

    @Test
    fun `CartItemForwardPayment pending confirmation stores catalog runtime data`() = runTest {
        // Arrange
        clearMocks(uxEvent, viewStateChange)
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemForwardPaymentSelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                transactionData = TransactionData(isPartnerManagedPurchase = false),
            ),
        )
        val event = captureForwardPaymentEvent()

        // Act
        event.onResult(DevicePayResult.PendingConfirmation(mapOf("total" to "86.79")))

        // Assert
        assertThat(runtimeState().catalogRuntimeData()).containsEntry("total", "86.79")
    }

    @Test
    fun `CartItemDevicePaySelected sends event with object data platform signal`() = runTest {
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
                    val platformEvent = events.single {
                        it.eventType == EventType.SignalCartItemInstantPurchaseInitiated
                    }
                    assertThat(platformEvent.parentGuid).isEqualTo("catalog-instance-guid-1")
                    assertThat(platformEvent.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                    assertThat(platformEvent.objectData).containsEntry("catalogItemId", "catalog-item-1")
                    assertThat(platformEvent.objectData).containsEntry("quantity", "1")
                    assertThat(platformEvent.eventData).isNull()
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
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Success)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", 1)
                },
            )
        }
        verify(exactly = 1, timeout = 2000) {
            platformEvent.invoke(
                match { events ->
                    events.containsSingleDevicePayTerminalEvent(EventType.SignalCartItemInstantPurchase)
                },
            )
        }

        // Act
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Failure)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", -1)
                },
            )
        }
        verify(exactly = 0, timeout = 500) {
            platformEvent.invoke(match { events -> events.containsTerminalInstantPurchaseEvent() })
        }

        // Act
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Retry)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("paymentResult", -1)
                },
            )
        }
        verify(exactly = 0, timeout = 500) {
            platformEvent.invoke(match { events -> events.containsTerminalInstantPurchaseEvent() })
        }
    }

    @Test
    fun `CartItemDevicePay failure callback sends failure platform signal once`() = runTest {
        // Arrange
        clearMocks(uxEvent, platformEvent, viewStateChange)
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
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Failure)

        // Assert
        verify(exactly = 1, timeout = 2000) {
            platformEvent.invoke(
                match { events ->
                    events.containsSingleDevicePayTerminalEvent(EventType.SignalCartItemInstantPurchaseFailure)
                },
            )
        }
    }

    @Test
    fun `CartItemDevicePay retry callback sends failure platform signal once`() = runTest {
        // Arrange
        clearMocks(uxEvent, platformEvent, viewStateChange)
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
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.Retry)

        // Assert
        verify(exactly = 1, timeout = 2000) {
            platformEvent.invoke(
                match { events ->
                    events.containsSingleDevicePayTerminalEvent(EventType.SignalCartItemInstantPurchaseFailure)
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
        clearMocks(platformEvent, viewStateChange)
        event.onResult(DevicePayResult.PendingConfirmation(mapOf("confirmationRef" to "confirmation-123")))
        event.onResult(DevicePayResult.Success)
        event.onResult(DevicePayResult.Failure)

        // Assert
        verify(timeout = 2000) {
            viewStateChange.invoke(
                withArg { state ->
                    assertThat(state.offerCustomStates["0"]).containsEntry("devicePayState", 1)
                },
            )
        }
        verify(exactly = 0, timeout = 500) {
            platformEvent.invoke(match { events -> events.containsTerminalInstantPurchaseEvent() })
        }
    }

    @Test
    fun `CartItemDevicePaySelected sends OfferProgression when validation passes`() = runTest {
        // Arrange
        clearMocks(platformEvent, uxEvent)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CartItemDevicePaySelected(
                offerId = 0,
                catalogItemModel = createCatalogItemProperties(),
                paymentProvider = PaymentProvider.GooglePay,
                transactionData = null,
                validatorFieldKeys = emptyList(),
            ),
        )

        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { platformEvent ->
                        assertThat(platformEvent.eventType).isEqualTo(EventType.SignalUserInteraction)
                        assertThat(platformEvent.parentGuid).isEqualTo("catalog-instance-guid-1")
                        assertThat(platformEvent.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                        assertThat(platformEvent.objectData).isEqualTo(
                            mapOf(
                                "action" to "OfferProgression",
                                "context" to "CustomStateValidationTriggerButton",
                                "interactionType" to "OfferProgression",
                            ),
                        )
                    }
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
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { platformEvent ->
                        assertThat(platformEvent.eventType).isEqualTo(EventType.SignalUserInteraction)
                        assertThat(platformEvent.parentGuid).isEqualTo("catalog-instance-guid-1")
                        assertThat(platformEvent.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                        assertThat(platformEvent.objectData).isEqualTo(
                            mapOf(
                                "action" to "ValidationTriggerFailed",
                                "context" to "CustomStateValidationTriggerButton",
                                "interactionType" to "ValidationTriggerFailed",
                            ),
                        )
                    }
                },
            )
        }
    }

    @Test
    fun `UserInteractionSelected sends layout scoped SignalUserInteraction with plugin parent guid`() = runTest {
        // Arrange
        clearMocks(platformEvent)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.UserInteractionSelected(
                offerId = 0,
                action = RoktUserInteractionAction.ToggleButtonStateTriggerClick,
                context = RoktUserInteractionContext.ToggleButtonStateTrigger,
            ),
        )

        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { platformEvent ->
                        assertThat(platformEvent.eventType).isEqualTo(EventType.SignalUserInteraction)
                        assertThat(platformEvent.parentGuid).isEqualTo("pluginInstanceGuid")
                        assertThat(platformEvent.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                        assertThat(platformEvent.objectData).isEqualTo(
                            mapOf(
                                "action" to "ToggleButtonStateTriggerClick",
                                "context" to "ToggleButtonStateTrigger",
                                "interactionType" to "ToggleButtonStateTriggerClick",
                            ),
                        )
                    }
                },
            )
        }
    }

    @Test
    fun `UserInteractionSelected sends SignalUserInteraction with object data`() = runTest {
        // Arrange
        clearMocks(platformEvent)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.UserInteractionSelected(
                offerId = 0,
                action = RoktUserInteractionAction.ThumbnailClick,
                context = RoktUserInteractionContext.CatalogImageGallery,
                parentGuid = "catalog-instance-guid-1",
            ),
        )

        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { platformEvent ->
                        assertThat(platformEvent.eventType).isEqualTo(EventType.SignalUserInteraction)
                        assertThat(platformEvent.parentGuid).isEqualTo("catalog-instance-guid-1")
                        assertThat(platformEvent.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                        assertThat(platformEvent.objectData).isEqualTo(
                            mapOf(
                                "action" to "ThumbnailClick",
                                "context" to "CatalogImageGallery",
                                "interactionType" to "ThumbnailClick",
                            ),
                        )
                    }
                },
            )
        }
    }

    @Test
    fun `UserInteractionSelected resolves catalog item parent guid from index`() = runTest {
        // Arrange
        clearMocks(platformEvent)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.UserInteractionSelected(
                offerId = 0,
                action = RoktUserInteractionAction.DropDownItemSelected,
                context = RoktUserInteractionContext.CatalogDropDown,
                catalogItemIndex = 1,
            ),
        )

        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { platformEvent ->
                        assertThat(platformEvent.eventType).isEqualTo(EventType.SignalUserInteraction)
                        assertThat(platformEvent.parentGuid).isEqualTo("catalog-instance-guid-2")
                        assertThat(platformEvent.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                        assertThat(platformEvent.objectData).isEqualTo(
                            mapOf(
                                "action" to "DropDownItemSelected",
                                "context" to "CatalogDropDown",
                                "interactionType" to "DropDownItemSelected",
                            ),
                        )
                    }
                },
            )
        }
    }

    @Test
    fun `CloseSelected with instant purchase dismissal method sends SignalInstantPurchaseDismissal`() = runTest {
        // Arrange
        clearMocks(platformEvent)

        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.CloseSelected(
                isDismissed = false,
                dismissalMethod = "INSTANT_PURCHASE_DISMISSED",
            ),
        )

        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { platformEvent ->
                        assertThat(platformEvent.eventType).isEqualTo(EventType.SignalInstantPurchaseDismissal)
                        assertThat(platformEvent.parentGuid).isEqualTo("pluginInstanceGuid")
                        assertThat(platformEvent.pageInstanceGuid).isEqualTo("pageInstanceGuid")
                        assertThat(platformEvent.metadata)
                            .anySatisfy { metadata ->
                                assertThat(metadata.name).isEqualTo("initiator")
                                assertThat(metadata.value).isEqualTo("INSTANT_PURCHASE_DISMISSED")
                            }
                    }
                },
            )
        }
    }

    @Test
    fun `CloseSelected without instant purchase dismissal method still sends SignalDismissal`() = runTest {
        // Arrange
        clearMocks(platformEvent)

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = false))

        // Assert
        verify(timeout = 2000) {
            platformEvent.invoke(
                withArg { events ->
                    assertThat(events).anySatisfy { platformEvent ->
                        assertThat(platformEvent.eventType).isEqualTo(EventType.SignalDismissal)
                        assertThat(platformEvent.parentGuid).isEqualTo("pluginInstanceGuid")
                        assertThat(platformEvent.metadata)
                            .anySatisfy { metadata ->
                                assertThat(metadata.name).isEqualTo("initiator")
                                assertThat(metadata.value).isEqualTo("CLOSE_BUTTON")
                            }
                    }
                },
            )
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

    private fun captureForwardPaymentEvent(): RoktUxEvent.CartItemForwardPayment {
        val eventSlot = slot<RoktUxEvent>()
        verify(timeout = 2000) {
            uxEvent.invoke(capture(eventSlot))
        }
        return eventSlot.captured as RoktUxEvent.CartItemForwardPayment
    }

    private fun runtimeState(): LayoutRuntimeState {
        val runtimeStateField = LayoutViewModel::class.java.getDeclaredField("runtimeState")
        runtimeStateField.isAccessible = true
        return runtimeStateField.get(layoutViewModel) as LayoutRuntimeState
    }

    private fun List<RoktPlatformEvent>.containsTerminalInstantPurchaseEvent(): Boolean = any { it.isTerminalInstantPurchaseEvent() }

    private fun List<RoktPlatformEvent>.containsSingleDevicePayTerminalEvent(eventType: EventType): Boolean {
        val event = filter { it.isTerminalInstantPurchaseEvent() }.singleOrNull() ?: return false
        return event.eventType == eventType &&
            event.parentGuid == "catalog-instance-guid-1" &&
            event.pageInstanceGuid == "pageInstanceGuid" &&
            event.eventData == null &&
            event.objectData == null
    }

    private fun RoktPlatformEvent.isTerminalInstantPurchaseEvent(): Boolean = eventType == EventType.SignalCartItemInstantPurchase ||
        eventType == EventType.SignalCartItemInstantPurchaseFailure

    private fun createCatalogItemProperties(
        instanceGuid: String = "catalog-instance-guid-1",
        catalogItemId: String = "catalog-item-1",
        price: Double = 79.99,
        originalPrice: Double = 99.99,
        token: String = "catalog-token-1",
    ): HMap = HMap().apply {
        set(TypedKey<String>("instanceGuid"), instanceGuid)
        set(TypedKey<String>("token"), token)
        set(TypedKey<String>("title"), "Everyday sneakers")
        set(TypedKey<String>("cartItemId"), "cart-item-1")
        set(TypedKey<String>("catalogItemId"), catalogItemId)
        set(TypedKey<String>("currency"), "USD")
        set(TypedKey<String>("description"), "Lightweight daily shoes")
        set(TypedKey<String>("linkedProductId"), "linked-product-1")
        set(TypedKey<String>("providerData"), "{\"merchant\":\"rokt\"}")
        set(TypedKey<Double>("price"), price)
        set(TypedKey<Double>("originalPrice"), originalPrice)
    }
}
