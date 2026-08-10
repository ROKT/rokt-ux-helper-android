package com.rokt.networkhelper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Caller-facing offers request. `integrationInfo` is accepted for source
 * compatibility but is not part of the `v2/sessions/offers` body.
 */
data class ExperienceRequest(
    val pageIdentifier: String,
    val attributes: Map<String, String>,
    val integrationInfo: String = "",
    val sessionId: String? = null,
    val packageName: String? = null,
)

/**
 * Request body for `POST v2/sessions/offers` (S2S flow). Only the fields the demo
 * populates are sent; optional blocks are omitted when null (the offers Json is
 * configured with `encodeDefaults = false`). Session identity comes from the
 * Authorization header, not the body.
 */
@Serializable
internal data class NetworkOffersRequest(
    @SerialName("channel") val channel: OffersChannel,
    @SerialName("page") val page: OffersPage,
    @SerialName("attributes") val attributes: Map<String, String> = emptyMap(),
    @SerialName("customer") val customer: OffersCustomer? = null,
    @SerialName("transaction") val transaction: OffersTransaction? = null,
    @SerialName("payment") val payment: OffersPayment? = null,
    @SerialName("device") val device: OffersDevice? = null,
)

@Serializable
internal data class OffersChannel(@SerialName("type") val type: String)

@Serializable
internal data class OffersPage(
    @SerialName("page_identifier") val pageIdentifier: String,
    @SerialName("package_name") val packageName: String? = null,
)

@Serializable
internal data class OffersCustomer(
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("language") val language: String? = null,
)

@Serializable
internal data class OffersTransaction(
    @SerialName("transaction_value") val transactionValue: Double? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("confirmation_ref") val confirmationRef: String? = null,
)

@Serializable
internal data class OffersPayment(@SerialName("type") val type: String? = null)

@Serializable
internal data class OffersDevice(
    @SerialName("user_agent") val userAgent: String? = null,
    @SerialName("ip") val ip: String? = null,
    @SerialName("language") val language: String? = null,
)
