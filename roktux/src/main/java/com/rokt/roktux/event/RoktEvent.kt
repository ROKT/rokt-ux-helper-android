package com.rokt.roktux.event

import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.modelmapper.utils.roktDateFormat
import com.rokt.roktux.RoktIntegrationConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

sealed interface RoktEvent

sealed interface RoktUxEvent {
    /**
     * OfferEngagement event will be triggered if User engaged with the offer
     * @param layoutId - layout identifier
     */
    data class OfferEngagement(val layoutId: String) : RoktUxEvent

    /**
     * PositiveEngagement event will be triggered if User positively engaged with the offer
     * @param layoutId - layout identifier
     */
    data class PositiveEngagement(val layoutId: String) : RoktUxEvent

    /**
     * LayoutInteractive event will be triggered when layout has been rendered and is interactable
     * @param layoutId - layout identifier
     */
    data class LayoutInteractive(val layoutId: String) : RoktUxEvent

    /**
     * LayoutReady event will be triggered when placement is ready to display but has not rendered content yet
     * @param layoutId - layout identifier
     */
    data class LayoutReady(val layoutId: String) : RoktUxEvent

    /**
     * LayoutClosed event will be triggered when placement closes by user
     * @param layoutId - layout identifier
     */
    data class LayoutClosed(val layoutId: String) : RoktUxEvent

    /**
     * LayoutCompleted event will be triggered when the offer progression moves to the end and no more
     * offer to display
     * @param layoutId - layout identifier
     */
    data class LayoutCompleted(val layoutId: String) : RoktUxEvent

    /**
     * LayoutFailure event will be triggered when placement could not be displayed due to some failure
     * @param layoutId - optional layout identifier
     */
    data class LayoutFailure(val layoutId: String? = null) : RoktUxEvent

    data class OpenUrl(
        val url: String,
        val id: String,
        val type: OpenLinks,
        val onClose: (id: String) -> Unit,
        val onError: (id: String, throwable: Throwable) -> Unit,
    ) : RoktUxEvent
}

@Serializable
data class RoktPlatformEvent(
    @SerialName("eventType") val eventType: EventType,
    @SerialName("sessionId") val sessionId: String,
    @SerialName("parentGuid") val parentGuid: String = "",
    @SerialName("pageInstanceGuid") val pageInstanceGuid: String = "",
    @SerialName("eventTime") val eventTime: String = roktDateFormat.format(Date()),
    @SerialName("eventData") val eventData: Map<String, String>? = null,
    @SerialName("metadata") var metadata: List<EventNameValue> = emptyList(),
) : RoktEvent {
    init {
        val fixedMetadata = listOf(
            EventNameValue(KEY_CAPTURE_METHOD, CLIENT_PROVIDED),
            EventNameValue(KEY_CLIENT_TIMESTAMP, eventTime),
        )
        this.metadata += fixedMetadata
    }

    fun toJsonString(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}

enum class EventType {
    @SerialName("SignalLoadComplete")
    SignalLoadComplete,

    @SerialName("SignalImpression")
    SignalImpression,

    @SerialName("SignalViewed")
    SignalViewed,

    @SerialName("SignalInitialize")
    SignalInitialize,

    @SerialName("SignalGatedResponse")
    SignalGatedResponse,

    @SerialName("SignalResponse")
    SignalResponse,

    @SerialName("SignalDismissal")
    SignalDismissal,

    @SerialName("SignalActivation")
    SignalActivation,

    @SerialName("SignalSdkDiagnostic")
    SignalSdkDiagnostic,
}

@Serializable
data class EventNameValue(@SerialName("name") val name: String, @SerialName("value") val value: String)

internal fun SignalType.toEventType(): EventType = when (this) {
    SignalType.SignalResponse -> EventType.SignalResponse
    SignalType.SignalGatedResponse -> EventType.SignalGatedResponse
}

@Serializable
data class RoktPlatformEventsWrapper(
    @SerialName("integration") val integration: RoktIntegrationConfig,
    @SerialName("events") val events: List<RoktPlatformEvent>,
) {
    fun toJsonString(): String = Json { encodeDefaults = true }.encodeToString(this)
}

private const val KEY_CAPTURE_METHOD = "captureMethod"
private const val KEY_CLIENT_TIMESTAMP = "clientTimeStamp"
private const val CLIENT_PROVIDED = "ClientProvided"
