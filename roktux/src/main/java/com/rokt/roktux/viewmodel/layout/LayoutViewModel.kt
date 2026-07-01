package com.rokt.roktux.viewmodel.layout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_ACTION
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_INSTANCE_GUID
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_IS_POSITIVE
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_SIGNAL_TYPE
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_TOKEN
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_URL
import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.modelmapper.uimodel.Action
import com.rokt.modelmapper.uimodel.ExperienceModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.modelmapper.uimodel.PluginModel
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.modelmapper.utils.DEFAULT_VIEWABLE_ITEMS
import com.rokt.modelmapper.utils.FIRST_OFFER_INDEX
import com.rokt.modelmapper.utils.roktDateFormat
import com.rokt.roktux.RoktViewState
import com.rokt.roktux.event.DevicePayResult
import com.rokt.roktux.event.EventNameValue
import com.rokt.roktux.event.EventType
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUserInteractionAction
import com.rokt.roktux.event.RoktUserInteractionContext
import com.rokt.roktux.event.RoktUxEvent
import com.rokt.roktux.event.UrlEventState
import com.rokt.roktux.event.toEventType
import com.rokt.roktux.logging.RoktUXLogger
import com.rokt.roktux.state.LayoutRuntimeState
import com.rokt.roktux.utils.chunk
import com.rokt.roktux.utils.isEmbedded
import com.rokt.roktux.validation.ValidationCoordinator
import com.rokt.roktux.viewmodel.base.BaseViewModel
import com.rokt.roktux.viewmodel.layout.LayoutContract.LayoutEvent.ResponseOptionSelected
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

internal class LayoutViewModel(
    private val location: String,
    private val startTimeStamp: Long,
    private val uxEvent: (uxEvent: RoktUxEvent) -> Unit,
    private val platformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
    private val viewStateChange: (state: RoktViewState) -> Unit,
    private val modelMapper: ModelMapper,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher,
    private val handleUrlByApp: Boolean,
    private var currentOffer: Int,
    customStates: Map<String, Int>,
    offerCustomStates: Map<String, Map<String, Int>>,
    domainStates: Map<String, Int>,
    validationCoordinator: ValidationCoordinator = ValidationCoordinator(),
    private var edgeToEdgeDisplay: Boolean,
    private val completedDevicePayCartItemIds: Set<String> = emptySet(),
) : BaseViewModel<LayoutContract.LayoutEvent, LayoutUiState, LayoutContract.LayoutEffect>() {

    private lateinit var pluginId: String
    private lateinit var experienceModel: ExperienceModel
    private lateinit var pluginModel: PluginModel
    private lateinit var pluginViewState: RoktViewState
    private var viewableItems: AtomicReference<Int> = AtomicReference(DEFAULT_VIEWABLE_ITEMS)
    private val runtimeState = LayoutRuntimeState(
        customStates = customStates,
        offerCustomStates = offerCustomStates,
        domainStates = domainStates,
        validationCoordinator = validationCoordinator,
    )

    // SDK's internal thread-safe structure to track URL states
    private val urlEventStateMap = ConcurrentHashMap<String, UrlEventState>()

    private val _eventsQueue = MutableSharedFlow<RoktPlatformEvent>(replay = 5)
    private val _sentEvents = mutableSetOf<RoktPlatformEvent>()
    private var pendingDevicePayCatalogItem: PendingDevicePayCatalogItem? = null

    init {
        // The buffer is a queue with max capacity of 20 and interval 25ms.
        // It queues the request in a chunk of 25ms and max buffer as 20 and sends
        // them together.
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _eventsQueue.chunk(EVENT_REQUEST_BUFFER_MILLIS, QUEUE_CAPACITY).collect { events ->
                    events.distinct().filterNot { _sentEvents.contains(it) }.takeIf { it.isNotEmpty() }?.let {
                        processEventQueue(it)
                    }
                }
            } finally {
                // If the composable exits and the VM is cleared, the job is cancelled while events may still be in the queue
                // This processes the final batch of events before the job is finally cancelled
                _eventsQueue.replayCache.distinct().filterNot { _sentEvents.contains(it) }.takeIf { it.isNotEmpty() }
                    ?.let {
                        withContext(NonCancellable) {
                            processEventQueue(it)
                        }
                    }
            }
        }
    }

    private fun handleExecuteEvent() {
        RoktUXLogger.debug { "Processing layout execute event for location: $location" }
        safeLaunch {
            withContext(ioDispatcher) {
                val response = modelMapper.transformResponse()
                if (response.isSuccess) {
                    RoktUXLogger.debug { "Experience response parsed successfully for location: $location" }
                    createLayoutState(currentOffer)
                } else {
                    response.exceptionOrNull()?.let { exception ->
                        RoktUXLogger.error(error = exception) {
                            "Failed to parse experience response for location: $location"
                        }
                        handleError(exception)
                    }
                }
            }
        }
    }

    private fun createLayoutState(currentOffer: Int = FIRST_OFFER_INDEX, viewableItems: Int = DEFAULT_VIEWABLE_ITEMS) {
        experienceModel = modelMapper.getSavedExperience() ?: return
        pluginModel = experienceModel.plugins.firstOrNull() ?: return
        var layoutSchema = pluginModel.outerLayoutSchema
        if (layoutSchema?.isEmbedded() == true &&
            !location.equals(
                pluginModel.targetElementSelector,
                ignoreCase = true,
            )
        ) {
            handleError(IllegalArgumentException(LOCATION_TARGET_ELEMENT_DOES_NOT_MATCH))
            return
        } else if (!edgeToEdgeDisplay) {
            if (layoutSchema is LayoutSchemaUiModel.OverlayUiModel) {
                layoutSchema = LayoutSchemaUiModel.OverlayUiModel(
                    layoutSchema.ownModifiers,
                    layoutSchema.containerProperties,
                    layoutSchema.conditionalTransitionModifiers,
                    layoutSchema.allowBackdropToClose,
                    layoutSchema.child,
                    edgeToEdgeDisplay,
                )
            } else if (layoutSchema is LayoutSchemaUiModel.BottomSheetUiModel) {
                layoutSchema = LayoutSchemaUiModel.BottomSheetUiModel(
                    layoutSchema.ownModifiers,
                    layoutSchema.containerProperties,
                    layoutSchema.conditionalTransitionModifiers,
                    layoutSchema.allowBackdropToClose,
                    layoutSchema.child,
                    edgeToEdgeDisplay,
                )
            }
        }
        val lastOfferIndex = pluginModel.slots.size - 1
        pluginId = pluginModel.id

        seedCompletedDevicePayOffers()

        if (layoutSchema != null && lastOfferIndex >= FIRST_OFFER_INDEX) {
            sendViewState(currentOffer)
            setSuccessState(
                LayoutUiState(
                    layoutSchema,
                    OfferUiState(
                        currentOfferIndex = currentOffer,
                        lastOfferIndex = lastOfferIndex,
                        viewableItems = viewableItems,
                        targetOfferIndex = currentOffer,
                        creativeCopy = persistentMapOf(),
                        breakpoints = pluginModel.breakpoint,
                        customState = runtimeState.globalCustomStates().toImmutableMap(),
                        catalogRuntimeData = runtimeState.catalogRuntimeData().toImmutableMap(),
                        domainStates = runtimeState.domainStates().toImmutableMap(),
                        offerCustomStates = runtimeState.immutableOfferCustomStates(),
                    ),
                ),
            )
        } else {
            // Handle case where layoutSchema is null
            uxEvent(RoktUxEvent.LayoutFailure())
        }
    }

    override suspend fun handleEvents(event: LayoutContract.LayoutEvent) {
        when (event) {
            LayoutContract.LayoutEvent.LayoutInitialised -> {
                RoktUXLogger.verbose { "Layout initialised for location: $location" }
                handleExecuteEvent()
            }

            is LayoutContract.LayoutEvent.LayoutReady -> {
                RoktUXLogger.info { "Layout ready for plugin: $pluginId" }
                uxEvent(RoktUxEvent.LayoutReady(pluginId))
                handlePlatformEvent(
                    RoktPlatformEvent(
                        eventType = EventType.SignalLoadComplete,
                        sessionId = experienceModel.sessionId,
                        parentGuid = pluginModel.instanceGuid,
                        token = pluginModel.token,
                    ),
                )
            }

            is LayoutContract.LayoutEvent.LayoutInteractive -> {
                RoktUXLogger.verbose { "Layout interactive for plugin: $pluginId" }
                uxEvent(RoktUxEvent.LayoutInteractive(pluginId))
            }

            is LayoutContract.LayoutEvent.UserInteracted -> {
                handlePlatformEvent(
                    RoktPlatformEvent(
                        eventType = EventType.SignalActivation,
                        sessionId = experienceModel.sessionId,
                        parentGuid = pluginModel.instanceGuid,
                        token = pluginModel.token,
                    ),
                )
            }

            LayoutContract.LayoutEvent.FirstOfferLoaded -> {
                handleFirstOfferLoaded()
            }

            is ResponseOptionSelected -> {
                handleResponseOptionSelected(
                    event.openLinks,
                    event.currentOffer,
                    event.responseOptionProperties,
                    event.shouldProgress,
                )
            }

            is LayoutContract.LayoutEvent.LayoutVariantSwiped -> {
                updateOffer(event.currentOffer)
            }

            is LayoutContract.LayoutEvent.LayoutVariantNavigated -> {
                updateTargetOffer(event.targetOffer)
            }

            is LayoutContract.LayoutEvent.SetCurrentOffer -> {
                updateOffer(event.currentOffer)
            }

            is LayoutContract.LayoutEvent.CloseSelected -> {
                if (event.dismissalMethod == INSTANT_PURCHASE_DISMISSED) {
                    sendInstantPurchaseDismissalEvent(event.dismissalMethod)
                } else {
                    sendDismissEvent(if (event.isDismissed) DISMISSED else CLOSE_BUTTON)
                }
                setEffect {
                    LayoutContract.LayoutEffect.CloseLayout(
                        onClose = {
                            uxEvent(RoktUxEvent.LayoutClosed(pluginId))
                        },
                    )
                }
                sendViewState(isDismissed = true)
            }

            is LayoutContract.LayoutEvent.UrlSelected -> {
                sendOpenUrlEvent(
                    url = event.url,
                    openLinks = event.linkOpenTarget,
                    shouldProgress = false,
                    isResponseUrl = false,
                )
            }

            is LayoutContract.LayoutEvent.ViewableItemsChanged -> {
                updateViewableItems(event.viewableItems)
            }

            is LayoutContract.LayoutEvent.SetCustomState -> {
                updateCustomState(event.key, event.value)
                sendViewState()
            }

            is LayoutContract.LayoutEvent.SetDomainState -> {
                updateDomainState(event.key, event.value)
                sendViewState()
            }

            is LayoutContract.LayoutEvent.SetActiveCatalogItem -> {
                updateActiveCatalogItem(event.index)
            }

            is LayoutContract.LayoutEvent.SignalViewed -> {
                handleSignalViewed(event.offerId)
            }

            is LayoutContract.LayoutEvent.SetOfferCustomState -> {
                runtimeState.replaceOfferCustomStates(event.offerId, event.customState)
                updateState { currentUiState ->
                    currentUiState.copy(
                        offerUiState = currentUiState.offerUiState.copy(
                            offerCustomStates = runtimeState.immutableOfferCustomStates(),
                        ),
                    )
                }
                sendViewState()
            }

            is LayoutContract.LayoutEvent.UiException -> {
                if (::experienceModel.isInitialized && event.closeLayout) {
                    setEffect {
                        LayoutContract.LayoutEffect.CloseLayout(
                            onClose = {
                                uxEvent(RoktUxEvent.LayoutFailure())
                            },
                        )
                    }
                }
                if (!(::experienceModel.isInitialized && experienceModel.options.useDiagnosticEvents)) {
                    return
                }
                handlePlatformEvent(
                    RoktPlatformEvent(
                        eventType = EventType.SignalSdkDiagnostic,
                        sessionId = experienceModel.sessionId,
                        parentGuid = pluginModel.instanceGuid,
                        eventData = mapOf(
                            KEY_STACKTRACE to event.throwable.toString(),
                        ),
                    ),
                )
            }

            is LayoutContract.LayoutEvent.CartItemInstantPurchaseSelected -> {
                handleCartItemInstancePurchaseSelected(event.catalogItemModel)
            }

            is LayoutContract.LayoutEvent.UserInteractionSelected -> {
                handleUserInteractionSelected(event)
            }

            is LayoutContract.LayoutEvent.CartItemForwardPaymentSelected -> {
                handleCartItemForwardPaymentSelected(event)
            }

            is LayoutContract.LayoutEvent.CartItemDevicePaySelected -> {
                handleCartItemDevicePaySelected(event)
            }

            is LayoutContract.LayoutEvent.CartItemDevicePayResultReceived -> {
                handleCartItemDevicePayResult(event.offerId, event.result)
            }

            is LayoutContract.LayoutEvent.CartItemForwardPaymentResultReceived -> {
                handleCartItemForwardPaymentResult(event.offerId, event.result)
            }

            else -> {}
        }
    }

    private fun handleSignalViewed(offerId: Int) {
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalViewed,
                sessionId = experienceModel.sessionId,
                parentGuid = pluginModel.slots[offerId].offer?.creative?.instanceGuid.orEmpty(),
                token = pluginModel.slots[offerId].offer?.creative?.token.orEmpty(),
                pageInstanceGuid = experienceModel.placementContext.pageInstanceGuid,
            ),
        )
    }

    private fun handleFirstOfferLoaded() {
        // layout impression
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalImpression,
                sessionId = experienceModel.sessionId,
                parentGuid = pluginModel.instanceGuid,
                token = pluginModel.token,
                metadata = listOf(
                    EventNameValue(KEY_PAGE_SIGNAL_LOAD_START, roktDateFormat.format(Date(startTimeStamp))),
                    EventNameValue(KEY_PAGE_RENDER_ENGINE, LAYOUTS_RENDER_ENGINE),
                    EventNameValue(
                        KEY_PAGE_SIGNAL_LOAD_COMPLETE,
                        roktDateFormat.format(Date(System.currentTimeMillis())),
                    ),
                ),
            ),
        )
        handleNextOfferLoaded(FIRST_OFFER_INDEX)
    }

    private fun handleNextOfferLoaded(offerIndex: Int) {
        for (id in offerIndex until offerIndex + viewableItems.get()) {
            // slot impression
            handlePlatformEvent(
                RoktPlatformEvent(
                    eventType = EventType.SignalImpression,
                    sessionId = experienceModel.sessionId,
                    parentGuid = pluginModel.slots[id].instanceGuid,
                    token = pluginModel.slots[id].token,
                ),
            )

            // Creative impression
            handlePlatformEvent(
                RoktPlatformEvent(
                    eventType = EventType.SignalImpression,
                    sessionId = experienceModel.sessionId,
                    parentGuid = pluginModel.slots[id].offer?.creative?.instanceGuid.orEmpty(),
                    token = pluginModel.slots[id].offer?.creative?.token.orEmpty(),
                ),
            )
        }
    }

    private fun handleCartItemInstancePurchaseSelected(catalogItemProperties: HMap) {
        val originalPrice = catalogItemProperties.originalPrice()
        uxEvent(
            RoktUxEvent.CartItemInstantPurchase(
                layoutId = pluginId,
                cartItemId = catalogItemProperties.cartItemId(),
                catalogItemId = catalogItemProperties.catalogItemId(),
                currency = catalogItemProperties.currency(),
                description = catalogItemProperties.description(),
                linkedProductId = catalogItemProperties.linkedProductId(),
                totalPrice = originalPrice,
                quantity = 1,
                unitPrice = originalPrice,
            ),
        )
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalCartItemInstantPurchaseInitiated,
                sessionId = experienceModel.sessionId,
                parentGuid = catalogItemProperties.instanceGuid(),
                token = catalogItemProperties.token(),
                eventData = catalogItemProperties.cartItemEventData(originalPrice),
            ),
        )
        setEvent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = false))
    }

    private fun handleCartItemForwardPaymentSelected(event: LayoutContract.LayoutEvent.CartItemForwardPaymentSelected) {
        val catalogItemProperties = event.catalogItemModel
        val salePrice = catalogItemProperties.salePrice()
        uxEvent(
            RoktUxEvent.CartItemForwardPayment(
                layoutId = pluginId,
                name = catalogItemProperties.name(),
                cartItemId = catalogItemProperties.cartItemId(),
                catalogItemId = catalogItemProperties.catalogItemId(),
                currency = catalogItemProperties.currency(),
                description = catalogItemProperties.description(),
                linkedProductId = catalogItemProperties.linkedProductId(),
                providerData = catalogItemProperties.providerData(),
                totalPrice = salePrice,
                quantity = 1,
                unitPrice = salePrice,
                transactionData = event.transactionData,
                onResult = { result ->
                    setEvent(
                        LayoutContract.LayoutEvent.CartItemForwardPaymentResultReceived(
                            event.offerId,
                            result,
                        ),
                    )
                },
            ),
        )
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalCartItemInstantPurchaseInitiated,
                sessionId = experienceModel.sessionId,
                parentGuid = catalogItemProperties.instanceGuid(),
                token = catalogItemProperties.token(),
                eventData = catalogItemProperties.cartItemEventData(salePrice),
            ),
        )
    }

    private fun handleCartItemDevicePaySelected(event: LayoutContract.LayoutEvent.CartItemDevicePaySelected) {
        if (!runtimeState.validationCoordinator.validate(event.validatorFieldKeys)) {
            sendUserInteractionEvent(
                parentGuid = event.catalogItemModel?.instanceGuid().orEmpty(),
                token = event.catalogItemModel?.token().orEmpty(),
                action = RoktUserInteractionAction.ValidationTriggerFailed,
                context = RoktUserInteractionContext.CustomStateValidationTriggerButton,
            )
            return
        }

        val catalogItemProperties = event.catalogItemModel ?: return
        val salePrice = catalogItemProperties.salePrice()
        pendingDevicePayCatalogItem = catalogItemProperties.toPendingDevicePayCatalogItem()
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalCartItemInstantPurchaseInitiated,
                sessionId = experienceModel.sessionId,
                parentGuid = catalogItemProperties.instanceGuid(),
                token = catalogItemProperties.token(),
                pageInstanceGuid = experienceModel.placementContext.pageInstanceGuid,
                objectData = catalogItemProperties.cartItemObjectData(),
            ),
        )
        uxEvent(
            RoktUxEvent.CartItemDevicePay(
                layoutId = pluginId,
                name = catalogItemProperties.name(),
                cartItemId = catalogItemProperties.cartItemId(),
                catalogItemId = catalogItemProperties.catalogItemId(),
                currency = catalogItemProperties.currency(),
                description = catalogItemProperties.description(),
                linkedProductId = catalogItemProperties.linkedProductId(),
                providerData = catalogItemProperties.providerData(),
                totalPrice = salePrice,
                quantity = 1,
                unitPrice = salePrice,
                paymentProvider = event.paymentProvider,
                transactionData = event.transactionData,
                onResult = { result ->
                    setEvent(LayoutContract.LayoutEvent.CartItemDevicePayResultReceived(event.offerId, result))
                },
            ),
        )
    }

    private fun handleCartItemDevicePayResult(offerId: Int, result: DevicePayResult) {
        when (result) {
            DevicePayResult.Success -> {
                updateOfferCustomState(offerId, PAYMENT_RESULT_CUSTOM_STATE_KEY, 1)
                sendDevicePayTerminalEvent(EventType.SignalCartItemInstantPurchase)
            }

            DevicePayResult.Failure,
            DevicePayResult.Retry,
            -> {
                updateOfferCustomState(offerId, PAYMENT_RESULT_CUSTOM_STATE_KEY, -1)
                sendDevicePayTerminalEvent(EventType.SignalCartItemInstantPurchaseFailure)
            }

            is DevicePayResult.PendingConfirmation -> {
                runtimeState.setCatalogRuntimeData(result.catalogRuntimeData)
                runtimeState.setOfferCustomState(offerId, DEVICE_PAY_STATE_CUSTOM_STATE_KEY, 1)
                updateState { currentUiState ->
                    currentUiState.copy(
                        offerUiState = currentUiState.offerUiState.copy(
                            offerCustomStates = runtimeState.immutableOfferCustomStates(),
                            catalogRuntimeData = runtimeState.catalogRuntimeData().toImmutableMap(),
                        ),
                    )
                }
                pendingDevicePayCatalogItem = null
            }
        }
        sendViewState()
    }

    private fun sendDevicePayTerminalEvent(eventType: EventType) {
        val catalogItem = pendingDevicePayCatalogItem ?: return
        pendingDevicePayCatalogItem = null
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = eventType,
                sessionId = experienceModel.sessionId,
                parentGuid = catalogItem.instanceGuid,
                token = catalogItem.token,
                pageInstanceGuid = experienceModel.placementContext.pageInstanceGuid,
            ),
        )
    }

    private fun handleCartItemForwardPaymentResult(offerId: Int, result: DevicePayResult) {
        when (result) {
            DevicePayResult.Success -> updateOfferCustomState(offerId, PAYMENT_RESULT_CUSTOM_STATE_KEY, 1)

            DevicePayResult.Failure,
            DevicePayResult.Retry,
            -> updateOfferCustomState(offerId, PAYMENT_RESULT_CUSTOM_STATE_KEY, -1)

            is DevicePayResult.PendingConfirmation -> {
                runtimeState.setCatalogRuntimeData(result.catalogRuntimeData)
            }
        }
        sendViewState()
    }

    private fun handleUserInteractionSelected(event: LayoutContract.LayoutEvent.UserInteractionSelected) {
        val parentGuid = event.parentGuid
            ?: event.catalogItemIndex?.let { index -> catalogItemInstanceGuid(event.offerId, index) }
            ?: pluginModel.instanceGuid
        val token = event.catalogItemIndex?.let { index -> catalogItemToken(event.offerId, index) }.orEmpty()
        sendUserInteractionEvent(
            parentGuid = parentGuid,
            token = token,
            action = event.action,
            context = event.context,
        )
    }

    private fun sendUserInteractionEvent(
        parentGuid: String,
        token: String,
        action: RoktUserInteractionAction,
        context: RoktUserInteractionContext,
    ) {
        if (parentGuid.isBlank()) return
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalUserInteraction,
                sessionId = experienceModel.sessionId,
                parentGuid = parentGuid,
                token = token,
                objectData = mapOf(
                    KEY_USER_INTERACTION_ACTION to action.name,
                    KEY_USER_INTERACTION_CONTEXT to context.name,
                ),
            ),
        )
    }

    private fun handleResponseOptionSelected(
        openLinks: OpenLinks,
        currentOffer: Int,
        responseOptionProperties: HMap,
        shouldProgress: Boolean,
    ) {
        uxEvent(RoktUxEvent.OfferEngagement(pluginId))
        with(responseOptionProperties) {
            if (get<Boolean>(KEY_IS_POSITIVE) == true) {
                uxEvent(RoktUxEvent.PositiveEngagement(pluginId))
            }
            val eventType = get<SignalType>(KEY_SIGNAL_TYPE)?.toEventType()
            val parentGuid = get<String>(KEY_INSTANCE_GUID)
            if (eventType != null && parentGuid != null) {
                handlePlatformEvent(
                    RoktPlatformEvent(
                        eventType = eventType,
                        sessionId = experienceModel.sessionId,
                        parentGuid = parentGuid,
                        token = token(),
                    ),
                )
            }
            if (get<Action>(KEY_ACTION) == Action.Url) {
                sendOpenUrlEvent(get<String>(KEY_URL).orEmpty(), openLinks, true, shouldProgress)
            } else {
                if (shouldProgress) {
                    updateTargetOffer(currentOffer + 1)
                }
            }
        }
    }

    private fun updateTargetOffer(targetOfferIndex: Int) {
        updateState { currentUiState ->
            if (targetOfferIndex in FIRST_OFFER_INDEX..currentUiState.offerUiState.lastOfferIndex) {
                sendViewState(targetOfferIndex)
                currentUiState.copy(
                    offerUiState = currentUiState.offerUiState.copy(targetOfferIndex = targetOfferIndex),
                )
            } else {
                if (pluginModel.settings.closeOnComplete) {
                    sendViewState(isDismissed = true)
                    sendDismissEvent(NO_MORE_OFFERS_TO_SHOW)
                    setEffect {
                        LayoutContract.LayoutEffect.CloseLayout(
                            onClose = {
                                uxEvent(RoktUxEvent.LayoutCompleted(pluginId))
                            },
                        )
                    }
                }
                currentUiState
            }
        }
    }

    private fun updateOffer(newOfferIndex: Int) {
        updateState { currentUiState ->
            if (newOfferIndex in FIRST_OFFER_INDEX..currentUiState.offerUiState.lastOfferIndex) {
                currentOffer = newOfferIndex
                sendViewState(newOfferIndex)
                handleNextOfferLoaded(currentOffer)
                currentUiState.copy(
                    offerUiState = currentUiState.offerUiState.copy(
                        currentOfferIndex = currentOffer,
                        targetOfferIndex = currentOffer,
                    ),
                )
            } else {
                if (pluginModel.settings.closeOnComplete) {
                    sendViewState(isDismissed = true)
                    sendDismissEvent(NO_MORE_OFFERS_TO_SHOW)
                    setEffect {
                        LayoutContract.LayoutEffect.CloseLayout(
                            onClose = {
                                uxEvent(RoktUxEvent.LayoutCompleted(pluginId))
                            },
                        )
                    }
                }
                currentUiState
            }
        }
    }

    private fun updateViewableItems(numItems: Int) {
        if (viewableItems.get() != numItems) {
            updateState { currentUiState ->
                viewableItems.set(numItems)
                currentUiState.copy(offerUiState = currentUiState.offerUiState.copy(viewableItems = numItems))
            }
        }
    }

    private fun updateCustomState(key: String, value: Int) {
        runtimeState.setGlobalCustomState(key, value)
        updateState { currentUiState ->
            currentUiState.copy(
                offerUiState = currentUiState.offerUiState.copy(
                    customState = runtimeState.globalCustomStates().toImmutableMap(),
                ),
            )
        }
    }

    private fun updateDomainState(key: String, value: Int) {
        runtimeState.setDomainState(key, value)
        updateState { currentUiState ->
            currentUiState.copy(
                offerUiState = currentUiState.offerUiState.copy(
                    domainStates = runtimeState.domainStates().toImmutableMap(),
                ),
            )
        }
    }

    private fun updateOfferCustomState(offerId: Int, key: String, value: Int) {
        runtimeState.setOfferCustomState(offerId, key, value)
        updateState { currentUiState ->
            currentUiState.copy(
                offerUiState = currentUiState.offerUiState.copy(
                    offerCustomStates = runtimeState.immutableOfferCustomStates(),
                ),
            )
        }
    }

    private fun updateActiveCatalogItem(index: Int) {
        updateState { currentUiState ->
            currentUiState.copy(
                offerUiState = currentUiState.offerUiState.copy(
                    activeCatalogItemIndex = index,
                ),
            )
        }
    }

    private fun sendDismissEvent(dismissReason: String) {
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalDismissal,
                sessionId = experienceModel.sessionId,
                parentGuid = pluginModel.instanceGuid,
                token = pluginModel.token,
                metadata = listOf(EventNameValue(KEY_INITIATOR, dismissReason)),
            ),
        )
    }

    private fun sendInstantPurchaseDismissalEvent(dismissReason: String) {
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalInstantPurchaseDismissal,
                sessionId = experienceModel.sessionId,
                parentGuid = pluginModel.instanceGuid,
                token = pluginModel.token,
                metadata = listOf(EventNameValue(KEY_INITIATOR, dismissReason)),
            ),
        )
    }

    override fun handleError(exception: Throwable) {
        RoktUXLogger.error(error = exception) { "Layout error occurred" }
        super.handleError(exception)
        uxEvent.invoke(RoktUxEvent.LayoutFailure())
        if (!(::experienceModel.isInitialized && experienceModel.options.useDiagnosticEvents)) {
            return
        }
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalSdkDiagnostic,
                sessionId = experienceModel.sessionId,
                parentGuid = pluginModel.instanceGuid,
                eventData = mapOf(
                    "stacktrace" to exception.stackTrace.toString() + exception.localizedMessage,
                ),
            ),
        )
    }

    private fun sendOpenUrlEvent(
        url: String,
        openLinks: OpenLinks,
        shouldProgress: Boolean,
        isResponseUrl: Boolean = false,
    ) {
        val id = UUID.randomUUID().toString()
        val urlEventState = UrlEventState(url, isResponseUrl)

        // Store the event state in the internal map
        urlEventStateMap[id] = urlEventState
        if (handleUrlByApp) {
            val openUrlEvent = RoktUxEvent.OpenUrl(
                url = url,
                id = id,
                layoutId = pluginId,
                type = openLinks,
                onClose = { urlId -> onUrlClosed(urlId, shouldProgress) }, // Pass the id to handle closure
                onError = { _, throwable ->
                    onUrlClosed(id, shouldProgress)
                    handleError(throwable)
                },
            )
            // Send the event to the application
            uxEvent(openUrlEvent)
        } else {
            when (openLinks) {
                OpenLinks.Internally -> {
                    setEffect {
                        LayoutContract.LayoutEffect.OpenUrlInternal(
                            url,
                            id,
                            { urlId -> onUrlClosed(urlId, shouldProgress) },
                        ) { _, throwable ->
                            onUrlClosed(id, shouldProgress)
                            handleError(throwable)
                        }
                    }
                }

                OpenLinks.Passthrough -> {
                    val openUrlEvent = RoktUxEvent.OpenUrl(
                        url = url,
                        id = id,
                        layoutId = pluginId,
                        type = openLinks,
                        onClose = { urlId -> onUrlClosed(urlId, shouldProgress) }, // Pass the id to handle closure
                        onError = { _, throwable ->
                            onUrlClosed(id, shouldProgress)
                            handleError(throwable)
                        },
                    )
                    // Send the event to the application
                    uxEvent(openUrlEvent)
                }

                else -> {
                    setEffect {
                        LayoutContract.LayoutEffect.OpenUrlExternal(
                            url,
                            id,
                            { urlId -> onUrlClosed(urlId, shouldProgress) },
                        ) { _, throwable ->
                            onUrlClosed(id, shouldProgress)
                            handleError(throwable)
                        }
                    }
                }
            }
        }
    }

    // Function to handle URL closure
    private fun onUrlClosed(urlId: String, shouldProgress: Boolean) {
        urlEventStateMap[urlId]?.let { urlEventState ->
            if (urlEventState.isClosed.compareAndSet(false, true)) {
                // Remove the state from the map if no longer needed
                urlEventStateMap.remove(urlId)
                if (urlEventState.responseUrl && shouldProgress) {
                    updateTargetOffer(currentOffer + 1)
                }
            }
        }
    }

    private suspend fun processEventQueue(events: List<RoktPlatformEvent>) {
        withContext(mainDispatcher) {
            try {
                platformEvent.invoke(events)
                _sentEvents.addAll(events)
            } catch (e: Exception) {
                _sentEvents.removeAll(events.toSet())
            }
        }
    }

    private fun handlePlatformEvent(event: RoktPlatformEvent) {
        viewModelScope.launch(ioDispatcher) {
            _eventsQueue.emit(event)
        }
    }

    private fun sendViewState(currentOffer: Int = this.currentOffer, isDismissed: Boolean = false) {
        pluginViewState = RoktViewState(
            pluginId = pluginId,
            customStates = runtimeState.globalCustomStates().toImmutableMap(),
            offerCustomStates = runtimeState.allOfferCustomStates().toImmutableMap(),
            domainStates = runtimeState.domainStates().toImmutableMap(),
            offerIndex = currentOffer,
            pluginDismissed = isDismissed,
        )
        viewStateChange(pluginViewState)
    }

    private fun LayoutRuntimeState.immutableOfferCustomStates() =
        allOfferCustomStates().mapValues { (_, value) -> value.toImmutableMap() }.toImmutableMap()

    private fun HMap.name(): String = get<String>(KEY_TITLE).orEmpty()

    private fun HMap.cartItemId(): String = get<String>(KEY_CART_ITEM_ID).orEmpty()

    private fun HMap.catalogItemId(): String = get<String>(KEY_CATALOG_ITEM_ID).orEmpty()

    private fun HMap.currency(): String = get<String>(KEY_CURRENCY).orEmpty()

    private fun HMap.description(): String = get<String>(KEY_DESCRIPTION).orEmpty()

    private fun HMap.linkedProductId(): String = get<String>(KEY_LINKED_PRODUCT_ID).orEmpty()

    private fun HMap.providerData(): String = get<String>(KEY_PROVIDER_DATA).orEmpty()

    private fun HMap.instanceGuid(): String = get<String>(KEY_INSTANCE_GUID).orEmpty()

    private fun HMap.token(): String = get<String>(KEY_TOKEN).orEmpty()

    private fun catalogItemInstanceGuid(offerId: Int, catalogItemIndex: Int): String? = catalogItemProperties(
        offerId,
        catalogItemIndex,
    )?.instanceGuid()

    private fun catalogItemToken(offerId: Int, catalogItemIndex: Int): String? = catalogItemProperties(
        offerId,
        catalogItemIndex,
    )?.token()

    private fun catalogItemProperties(offerId: Int, catalogItemIndex: Int): HMap? = pluginModel.slots
        .getOrNull(offerId)
        ?.offer
        ?.catalogItems
        ?.getOrNull(catalogItemIndex)
        ?.properties

    /**
     * Seeds offers whose device-pay purchase already completed into their post-purchase state
     * (`paymentResult = 1`) before the first layout state is emitted, so the confirmation renders
     * immediately when the host is recreated mid-checkout. Reuses the same offer custom state the
     * live [handleCartItemDevicePayResult] success path sets; unmatched cart item ids are ignored.
     */
    private fun seedCompletedDevicePayOffers() {
        if (completedDevicePayCartItemIds.isEmpty()) return
        completedDevicePayCartItemIds.forEach { cartItemId ->
            val offerIndex = pluginModel.slots.indexOfFirst { slot ->
                slot.offer?.catalogItems?.any { it.properties.cartItemId() == cartItemId } == true
            }
            if (offerIndex >= FIRST_OFFER_INDEX) {
                runtimeState.setOfferCustomState(offerIndex, PAYMENT_RESULT_CUSTOM_STATE_KEY, 1)
            }
        }
    }

    private fun HMap.originalPrice(): Double = get<Double>(KEY_ORIGINAL_PRICE) ?: 0.0

    private fun HMap.salePrice(): Double = get<Double>(KEY_PRICE) ?: originalPrice()

    private fun HMap.cartItemEventData(price: Double): Map<String, String> = mapOf(
        KEY_CART_ITEM_ID to cartItemId(),
        KEY_CATALOG_ITEM_ID to catalogItemId(),
        KEY_CURRENCY to currency(),
        KEY_DESCRIPTION to description(),
        KEY_LINKED_PRODUCT_ID to linkedProductId(),
        KEY_TOTAL_PRICE to price.toString(),
        KEY_QUANTITY to "1",
        KEY_UNIT_PRICE to price.toString(),
    )

    private fun HMap.cartItemObjectData(): Map<String, String> = mapOf(
        KEY_CATALOG_ITEM_ID to catalogItemId(),
        KEY_QUANTITY to "1",
    )

    private fun HMap.toPendingDevicePayCatalogItem(): PendingDevicePayCatalogItem =
        PendingDevicePayCatalogItem(instanceGuid = instanceGuid(), token = token())

    private data class PendingDevicePayCatalogItem(val instanceGuid: String, val token: String)

    companion object {
        private const val KEY_INITIATOR = "initiator"
        private const val KEY_USER_INTERACTION_ACTION = "action"
        private const val KEY_USER_INTERACTION_CONTEXT = "context"
        private const val KEY_PAGE_RENDER_ENGINE = "pageRenderEngine"
        private const val KEY_PAGE_SIGNAL_LOAD_START = "pageSignalLoadStart"
        private const val KEY_PAGE_SIGNAL_LOAD_COMPLETE = "pageSignalLoadComplete"
        private const val KEY_STACKTRACE = "stacktrace"
        private const val LAYOUTS_RENDER_ENGINE = "Layouts"
        private const val NO_MORE_OFFERS_TO_SHOW = "NO_MORE_OFFERS_TO_SHOW"
        private const val DISMISSED = "DISMISSED"
        private const val CLOSE_BUTTON = "CLOSE_BUTTON"
        private const val INSTANT_PURCHASE_DISMISSED = "INSTANT_PURCHASE_DISMISSED"
        private const val LOCATION_TARGET_ELEMENT_DOES_NOT_MATCH =
            "Plugin targetElementSelector does not match the location"
        private const val QUEUE_CAPACITY = 20
        private const val EVENT_REQUEST_BUFFER_MILLIS = 25L

        // Cart Item Instant Purchase Properties
        private const val KEY_CART_ITEM_ID = "cartItemId"
        private const val KEY_CATALOG_ITEM_ID = "catalogItemId"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_LINKED_PRODUCT_ID = "linkedProductId"
        private const val KEY_TITLE = "title"
        private const val KEY_PRICE = "price"
        private const val KEY_ORIGINAL_PRICE = "originalPrice"
        private const val KEY_PROVIDER_DATA = "providerData"
        private const val KEY_TOTAL_PRICE = "totalPrice"
        private const val KEY_QUANTITY = "quantity"
        private const val KEY_UNIT_PRICE = "unitPrice"
        private const val PAYMENT_RESULT_CUSTOM_STATE_KEY = "paymentResult"
        private const val DEVICE_PAY_STATE_CUSTOM_STATE_KEY = "devicePayState"
    }

    class RoktViewModelFactory(
        private val location: String,
        private val startTimeStamp: Long,
        private val uxEvent: (uxEvent: RoktUxEvent) -> Unit,
        private val platformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
        private val viewStateChange: (state: RoktViewState) -> Unit,
        private val modelMapper: ModelMapper,
        private val ioDispatcher: CoroutineDispatcher,
        private val mainDispatcher: CoroutineDispatcher,
        private val handleUrlByApp: Boolean,
        private val currentOffer: Int,
        private val customStates: Map<String, Int>,
        private val offerCustomStates: Map<String, Map<String, Int>>,
        private val domainStates: Map<String, Int>,
        private val validationCoordinator: ValidationCoordinator = ValidationCoordinator(),
        private val edgeToEdgeDisplay: Boolean,
        private val completedDevicePayCartItemIds: Set<String> = emptySet(),
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(LayoutViewModel::class.java)) {
                return LayoutViewModel(
                    location = location,
                    startTimeStamp = startTimeStamp,
                    uxEvent = uxEvent,
                    platformEvent = platformEvent,
                    viewStateChange = viewStateChange,
                    modelMapper = modelMapper,
                    ioDispatcher = ioDispatcher,
                    mainDispatcher = mainDispatcher,
                    handleUrlByApp = handleUrlByApp,
                    currentOffer = currentOffer,
                    customStates = customStates,
                    offerCustomStates = offerCustomStates,
                    domainStates = domainStates,
                    validationCoordinator = validationCoordinator,
                    edgeToEdgeDisplay = edgeToEdgeDisplay,
                    completedDevicePayCartItemIds = completedDevicePayCartItemIds,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel type")
        }
    }
}
