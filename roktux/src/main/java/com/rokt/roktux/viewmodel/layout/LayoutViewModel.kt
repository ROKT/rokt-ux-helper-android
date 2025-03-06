package com.rokt.roktux.viewmodel.layout

import androidx.lifecycle.AtomicReference
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
import com.rokt.roktux.event.EventNameValue
import com.rokt.roktux.event.EventType
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent
import com.rokt.roktux.event.UrlEventState
import com.rokt.roktux.event.toEventType
import com.rokt.roktux.utils.chunk
import com.rokt.roktux.utils.isEmbedded
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
    private var customStates: Map<String, Int>,
    private var offerCustomStates: Map<String, Map<String, Int>>,
    private var edgeToEdgeDisplay: Boolean,
) : BaseViewModel<LayoutContract.LayoutEvent, LayoutUiState, LayoutContract.LayoutEffect>() {

    private lateinit var pluginId: String
    private lateinit var experienceModel: ExperienceModel
    private lateinit var pluginModel: PluginModel
    private lateinit var pluginViewState: RoktViewState
    private var viewableItems: AtomicReference<Int> = AtomicReference(DEFAULT_VIEWABLE_ITEMS)

    // SDK's internal thread-safe structure to track URL states
    private val urlEventStateMap = ConcurrentHashMap<String, UrlEventState>()

    private val _eventsQueue = MutableSharedFlow<RoktPlatformEvent>(replay = 5)
    private val _sentEvents = mutableSetOf<RoktPlatformEvent>()

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
        safeLaunch {
            withContext(ioDispatcher) {
                val response = modelMapper.transformResponse()
                if (response.isSuccess) {
                    createLayoutState(currentOffer)
                } else {
                    response.exceptionOrNull()?.let { exception ->
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
                        customState = customStates.toImmutableMap(),
                        offerCustomStates = offerCustomStates.mapValues { it.value.toImmutableMap() }.toImmutableMap(),
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
                handleExecuteEvent()
            }

            is LayoutContract.LayoutEvent.LayoutReady -> {
                uxEvent(RoktUxEvent.LayoutReady(pluginId))
                handlePlatformEvent(
                    RoktPlatformEvent(
                        eventType = EventType.SignalLoadComplete,
                        sessionId = experienceModel.sessionId,
                        parentGuid = pluginModel.instanceGuid,
                    ),
                )
            }

            is LayoutContract.LayoutEvent.LayoutInteractive -> {
                uxEvent(RoktUxEvent.LayoutInteractive(pluginId))
            }

            is LayoutContract.LayoutEvent.UserInteracted -> {
                handlePlatformEvent(
                    RoktPlatformEvent(
                        eventType = EventType.SignalActivation,
                        sessionId = experienceModel.sessionId,
                        parentGuid = pluginModel.instanceGuid,
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
                sendDismissEvent(if (event.isDismissed) DISMISSED else CLOSE_BUTTON)
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

            is LayoutContract.LayoutEvent.SignalViewed -> {
                handleSignalViewed(event.offerId)
            }

            is LayoutContract.LayoutEvent.SetOfferCustomState -> {
                offerCustomStates += (event.offerId.toString() to event.customState)
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

            else -> {}
        }
    }

    private fun handleSignalViewed(offerId: Int) {
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalViewed,
                sessionId = experienceModel.sessionId,
                parentGuid = pluginModel.slots[offerId].offer?.creative?.instanceGuid.orEmpty(),
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
                ),
            )

            // Creative impression
            handlePlatformEvent(
                RoktPlatformEvent(
                    eventType = EventType.SignalImpression,
                    sessionId = experienceModel.sessionId,
                    parentGuid = pluginModel.slots[id].offer?.creative?.instanceGuid.orEmpty(),
                ),
            )
        }
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
        customStates += (key to value)
        updateState { currentUiState ->
            currentUiState.copy(
                offerUiState = currentUiState.offerUiState.copy(customState = customStates.toImmutableMap()),
            )
        }
    }

    private fun sendDismissEvent(dismissReason: String) {
        handlePlatformEvent(
            RoktPlatformEvent(
                eventType = EventType.SignalDismissal,
                sessionId = experienceModel.sessionId,
                parentGuid = pluginModel.instanceGuid,
                metadata = listOf(EventNameValue(KEY_INITIATOR, dismissReason)),
            ),
        )
    }

    override fun handleError(exception: Throwable) {
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
            setEffect {
                if (openLinks == OpenLinks.Internally) {
                    LayoutContract.LayoutEffect.OpenUrlInternal(
                        url,
                        id,
                        { urlId -> onUrlClosed(urlId, shouldProgress) },
                    ) { _, throwable ->
                        onUrlClosed(id, shouldProgress)
                        handleError(throwable)
                    }
                } else {
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
            customStates = customStates.toImmutableMap(),
            offerCustomStates = offerCustomStates.toImmutableMap(),
            offerIndex = currentOffer,
            pluginDismissed = isDismissed,
        )
        viewStateChange(pluginViewState)
    }

    companion object {
        private const val KEY_INITIATOR = "initiator"
        private const val KEY_PAGE_RENDER_ENGINE = "pageRenderEngine"
        private const val KEY_PAGE_SIGNAL_LOAD_START = "pageSignalLoadStart"
        private const val KEY_PAGE_SIGNAL_LOAD_COMPLETE = "pageSignalLoadComplete"
        private const val KEY_STACKTRACE = "stacktrace"
        private const val LAYOUTS_RENDER_ENGINE = "Layouts"
        private const val NO_MORE_OFFERS_TO_SHOW = "NO_MORE_OFFERS_TO_SHOW"
        private const val DISMISSED = "DISMISSED"
        private const val CLOSE_BUTTON = "CLOSE_BUTTON"
        private const val LOCATION_TARGET_ELEMENT_DOES_NOT_MATCH =
            "Plugin targetElementSelector does not match the location"
        private const val QUEUE_CAPACITY = 20
        private const val EVENT_REQUEST_BUFFER_MILLIS = 25L
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
        private val edgeToEdgeDisplay: Boolean,
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
                    edgeToEdgeDisplay = edgeToEdgeDisplay,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel type")
        }
    }
}
