package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTransactionData(
    @SerialName("shippingAddress") val shippingAddress: NetworkAddress? = null,
    @SerialName("billingAddress") val billingAddress: NetworkAddress? = null,
    @SerialName("paymentType") val paymentType: String? = null,
    @SerialName("supportedPaymentMethods") val supportedPaymentMethods: List<NetworkPaymentMethod>? = null,
    @SerialName("isPartnerManagedPurchase") val isPartnerManagedPurchase: Boolean = true,
    @SerialName("partnerPaymentReference") val partnerPaymentReference: String? = null,
    @SerialName("confirmationRef") val confirmationRef: String? = null,
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap(),
)

@Serializable
data class NetworkPaymentMethod(@SerialName("type") val type: String)

@Serializable
data class NetworkAddress(
    @SerialName("name") val name: String = "",
    @SerialName("address1") val address1: String = "",
    @SerialName("address2") val address2: String? = null,
    @SerialName("city") val city: String = "",
    @SerialName("state") val state: String = "",
    @SerialName("stateCode") val stateCode: String = "",
    @SerialName("country") val country: String = "",
    @SerialName("countryCode") val countryCode: String = "",
    @SerialName("zip") val zip: String? = null,
)
