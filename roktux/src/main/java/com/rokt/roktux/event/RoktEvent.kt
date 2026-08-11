package com.rokt.roktux.event

import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.modelmapper.uimodel.TransactionData
import com.rokt.modelmapper.utils.roktDateFormat
import com.rokt.network.model.PaymentProvider
import com.rokt.roktux.RoktIntegrationConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID

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
     * LayoutFailure event will be triggered when placement could not be displayed due to some failure.
     *
     * Inspect [reason] to distinguish "no offer returned" (not an integration bug)
     * from rendering or host-integration failures.
     *
     * @param layoutId - optional layout identifier
     * @param sessionId - session identifier from the experience response, when known
     * @param reason - why the layout could not be shown
     */
    data class LayoutFailure(val layoutId: String? = null, val sessionId: String? = null, val reason: Reason) :
        RoktUxEvent {
        /**
         * Why a layout could not be displayed.
         */
        enum class Reason {
            /**
             * Experience response decoded but contained no renderable offers/layouts.
             * Not an integration bug — contact your account manager with [LayoutFailure.sessionId].
             */
            NoOffers,

            /**
             * Experience response could not be decoded or mapped.
             */
            InvalidResponse,

            /**
             * Layout schema failed validation/transform or a runtime render failure occurred.
             */
            InvalidSchema,

            /**
             * Host app location does not match the embedded placement [targetElementSelector].
             */
            MissingEmbeddedTarget,

            /**
             * Overlay/bottom sheet could not be presented.
             * Included for cross-platform parity; unused on Android Compose.
             */
            PresentationFailed,
        }
    }

    /**
     * OpenUrl event will be triggered when user clicks on a link or button with a Url action
     * @param url - url to open
     * @param id - internal identifier of the url
     * @param layoutId - layout identifier
     * @param type - type of the url. internal(internal web browser), external(external web browser), passthrough
     * @param onClose - callback when url is closed
     * @param onError - callback when url fails to open
     */
    @Serializable
    data class CartItemInstantPurchase(
        @SerialName("layoutId") val layoutId: String,
        @SerialName("cartItemId") val cartItemId: String,
        @SerialName("catalogItemId") val catalogItemId: String,
        @SerialName("currency") val currency: String,
        @SerialName("description") val description: String,
        @SerialName("linkedProductId") val linkedProductId: String,
        @SerialName("totalPrice") val totalPrice: Double,
        @SerialName("quantity") val quantity: Int,
        @SerialName("unitPrice") val unitPrice: Double,
    ) : RoktUxEvent {
        fun toJsonString(): String = Json { encodeDefaults = true }.encodeToString(this)
    }

    data class OpenUrl(
        val url: String,
        val id: String,
        val layoutId: String,
        val type: OpenLinks,
        val onClose: (id: String) -> Unit,
        val onError: (id: String, throwable: Throwable) -> Unit,
    ) : RoktUxEvent

    data class CartItemDevicePay(
        val layoutId: String,
        val name: String,
        val cartItemId: String,
        val catalogItemId: String,
        val currency: String,
        val description: String,
        val linkedProductId: String,
        val providerData: String,
        val totalPrice: Double,
        val quantity: Int,
        val unitPrice: Double,
        val paymentProvider: PaymentProvider,
        val transactionData: TransactionData?,
        val onResult: (DevicePayResult) -> Unit,
    ) : RoktUxEvent

    data class CartItemForwardPayment(
        val layoutId: String,
        val name: String,
        val cartItemId: String,
        val catalogItemId: String,
        val currency: String,
        val description: String,
        val linkedProductId: String,
        val providerData: String,
        val totalPrice: Double,
        val quantity: Int,
        val unitPrice: Double,
        val transactionData: TransactionData?,
        val onResult: (DevicePayResult) -> Unit,
    ) : RoktUxEvent
}

sealed interface DevicePayResult {
    object Success : DevicePayResult
    object Failure : DevicePayResult
    object Retry : DevicePayResult
    data class PendingConfirmation(val catalogRuntimeData: Map<String, String>) : DevicePayResult
}

@Serializable
data class RoktPlatformEvent(
    @SerialName("eventType") val eventType: EventType,
    @SerialName("sessionId") val sessionId: String,
    @SerialName("parentGuid") val parentGuid: String = "",
    @SerialName("token") val token: String = "",
    @SerialName("pageInstanceGuid") val pageInstanceGuid: String = "",
    @SerialName("eventTime") val eventTime: String = roktDateFormat.format(Date()),
    @SerialName("eventData") val eventData: Map<String, String>? = null,
    @SerialName("objectData") val objectData: Map<String, String>? = null,
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

    @SerialName("SignalCartItemInstantPurchaseInitiated")
    SignalCartItemInstantPurchaseInitiated,

    @SerialName("SignalCartItemInstantPurchase")
    SignalCartItemInstantPurchase,

    @SerialName("SignalCartItemInstantPurchaseFailure")
    SignalCartItemInstantPurchaseFailure,

    @SerialName("SignalInstantPurchaseDismissal")
    SignalInstantPurchaseDismissal,

    @SerialName("SignalUserInteraction")
    SignalUserInteraction,
}

@Serializable
data class EventNameValue(@SerialName("name") val name: String, @SerialName("value") val value: String)

internal enum class RoktUserInteractionAction {
    OfferProgression,
    ValidationTriggerFailed,
    DropDownItemSelected,
    ThumbnailClick,
    MainImageScrollIconLeftClick,
    MainImageScrollIconRightClick,
    MainImageSwipeLeft,
    MainImageSwipeRight,
    ToggleButtonStateTriggerClick,
}

internal enum class RoktUserInteractionContext {
    CustomStateValidationTriggerButton,
    CatalogDropDown,
    CatalogImageGallery,
    ToggleButtonStateTrigger,
}

internal fun SignalType.toEventType(): EventType = when (this) {
    SignalType.SignalResponse -> EventType.SignalResponse
    SignalType.SignalGatedResponse -> EventType.SignalGatedResponse
}

@Serializable
data class RoktPlatformEventsWrapper(
    @SerialName("integration") val integration: RoktIntegrationConfig,
    @SerialName("events") val events: List<RoktPlatformEvent>,
) {
    /**
     * Serialises these events in the `POST v2/sessions/events` shape so the payload can
     * be forwarded to the events API directly, without any further transformation.
     */
    fun toJsonString(): String = Json { encodeDefaults = true }.encodeToString(
        SessionEventsBody(events = events.map { it.toSessionEvent() }),
    )
}

// --- v2/sessions/events wire shape ------------------------------------------

@Serializable
private data class SessionEventsBody(
    @SerialName("channel") val channel: SessionEventChannel = SessionEventChannel(),
    // Pins the whole batch to the synchronous events path so every event lands on the same
    // session (instead of the async intake path, which mints a fresh session per event).
    @SerialName("single_session") val singleSession: Boolean = true,
    @SerialName("events") val events: List<SessionEvent>,
)

@Serializable
private data class SessionEventChannel(@SerialName("type") val type: String = CHANNEL_TYPE_S2S)

@Serializable
private data class SessionEvent(
    @SerialName("event_type") val eventType: String,
    @SerialName("instance_id") val instanceId: String,
    @SerialName("session_id") val sessionId: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("data") val data: Map<String, String>,
)

private data class RegistryEventType(val type: String, val extraData: Map<String, String> = emptyMap())

private fun RoktPlatformEvent.toSessionEvent(): SessionEvent {
    val mapped = eventType.toRegistryEventType()
    val data = buildMap {
        eventData?.forEach { (key, value) -> put(key, value) }
        objectData?.forEach { (key, value) -> put(key, value) }
        metadata.forEach { (name, value) ->
            when (name) {
                KEY_CLIENT_TIMESTAMP -> Unit

                // promoted to the top-level timestamp
                KEY_CAPTURE_METHOD -> put("capture_method", value)

                else -> put(name, value)
            }
        }
        // parentGuid is already the bare instance_guid (no `type:` prefix).
        put("parent_id", parentGuid)
        put("token", token)
        if (pageInstanceGuid.isNotEmpty()) put("page_instance_guid", pageInstanceGuid)
        mapped.extraData.forEach { (key, value) -> put(key, value) }
    }
    return SessionEvent(
        eventType = mapped.type,
        instanceId = UUID.randomUUID().toString(),
        // Canonical envelope field (not part of `data`). Carries the session on the
        // S2S self-forward path, where there is no session JWT to derive it from.
        sessionId = sessionId,
        timestamp = runCatching { roktDateFormat.parse(eventTime)?.time }.getOrNull() ?: System.currentTimeMillis(),
        data = data,
    )
}

/** Fixed legacy-enum → Session API event-type string mapping (partner-independent). */
private fun EventType.toRegistryEventType(): RegistryEventType = when (this) {
    EventType.SignalImpression -> RegistryEventType("impression")
    EventType.SignalViewed -> RegistryEventType("viewed")
    EventType.SignalResponse -> RegistryEventType("signal_response")
    EventType.SignalGatedResponse -> RegistryEventType("signal_gated_response")
    EventType.SignalDismissal -> RegistryEventType("dismissal")
    EventType.SignalInitialize -> RegistryEventType("signal_initialize")
    EventType.SignalLoadComplete -> RegistryEventType("load_complete")
    EventType.SignalActivation -> RegistryEventType("user_interaction", mapOf("interaction_type" to "activation"))
    EventType.SignalUserInteraction -> RegistryEventType("user_interaction")
    EventType.SignalSdkDiagnostic -> RegistryEventType("sdk_diagnostic")
    EventType.SignalCartItemInstantPurchase -> RegistryEventType("cart_item_instant_purchase")
    EventType.SignalCartItemInstantPurchaseFailure -> RegistryEventType("cart_item_instant_purchase_failure")
    EventType.SignalCartItemInstantPurchaseInitiated -> RegistryEventType("cart_item_instant_purchase_initiated")
    EventType.SignalInstantPurchaseDismissal -> RegistryEventType("instant_purchase_dismissal")
}

private const val KEY_CAPTURE_METHOD = "captureMethod"
private const val KEY_CLIENT_TIMESTAMP = "clientTimeStamp"
private const val CLIENT_PROVIDED = "ClientProvided"
private const val CHANNEL_TYPE_S2S = "s2s"
